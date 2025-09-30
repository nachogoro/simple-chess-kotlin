plugins {
    kotlin("jvm") version "2.2.20"
    `java-library`
    `maven-publish`
    signing
}

group = "io.github.nachogoro"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(11)
    explicitApi()
}

dependencies {
    // JNA for native library access
    api("net.java.dev.jna:jna:5.13.0")
    api("net.java.dev.jna:jna-platform:5.13.0")

    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    // Test dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

// Copy native libraries to resources during build
tasks.register<Copy>("copyNativeLibraries") {
    from("native")
    into("src/main/resources/native")
    include("**/*.so", "**/*.dll", "**/*.dylib")
}

// Ensure native libraries are copied before processing resources
tasks.processResources {
    dependsOn("copyNativeLibraries")
}

// Fix task dependency for sourcesJar
tasks.named("sourcesJar") {
    dependsOn("copyNativeLibraries")
}

// Configure JAR to include native libraries
tasks.jar {
    archiveBaseName.set("simple-chess-games")

    // Handle duplicate files in JAR
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "Simple Chess Games",
            "Bundle-SymbolicName" to "${project.group}.${project.name}",
            "Bundle-Version" to project.version,
            "Automatic-Module-Name" to "simple.chess.games"
        )
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            artifactId = "simple-chess-games"

            pom {
                name.set("Simple Chess Games")
                description.set("A JNA-based chess library for JVM and Android")
                url.set("https://github.com/nachogoro/simple-chess-kotlin")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("NachoGoro")
                        name.set("NachoGoro")
                        email.set("nachogoro.dev@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/nachogoro/simple-chess-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com:nachogoro/simple-chess-kotlin.git")
                    url.set("https://github.com/nachogoro/simple-chess-kotlin")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nachogoro/simple-chess-kotlin")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

// Signing configuration for Maven Central (optional)
signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

// Android compatibility
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn"
        )
    }
}
