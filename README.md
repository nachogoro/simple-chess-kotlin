# Kotlin Chess Library

A type-safe, functional Kotlin wrapper for the simple-chess-games C library
using JNA for seamless native interoperability.

## Features

- **Immutable design:** all game operations return new instances, never mutate
  existing state
- **Type-safe API:** sealed interfaces prevent direct instantiation.
- **Result/Either pattern:** no exceptions, functional error handling via
  `ChessResult<T>`
- **Automatic memory management:** automatically handles the memory (unlike
  when using the C API directly)
- **Automatic platform detection:** automatically detects the platform to load
  the right native binary.

## Quick Start

### 1. Build the Library

```bash
# Build the entire project
./gradlew build
```

After running `./gradlew build`, the main artifact is
`build/libs/simple-chess-kotlin.jar`. It contains:
  - Compiled Kotlin and Java classes
  - Native binaries for all supported platforms (Linux, Windows, Android)

### 2. API overview

- **`Game`:** sealed interface representing a chess game state (users cannot instantiate directly)
- **`ChessResult<T>:`** result type for error handling (Success/Error)
- **`Square`:** value class representing board squares ("e4", "a1", etc.)
- **`Piece`:** value class representing chess pieces with type and color
- **`Move`:** data class representing moves (regular moves and pawn promotions)
- **`Position`:** complete game position including FEN, castling rights, etc.

### 3. Adding to Your Project

Add the library JAR and JNA dependencies to your Gradle build:

```kotlin
dependencies {
    // Simple Chess Games library
    implementation(files("libs/simple-chess-kotlin.jar"))

    // JNA dependencies
    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("net.java.dev.jna:jna-platform:5.13.0")
}
```

The library includes native binaries for all supported platforms (Linux,
Windows, Android) in multiple architectures. These are automatically extracted
and loaded by the JNA interface - no manual path configuration is required.

## Dependencies

- **JNA 5.13.0** - For native library access
- **Kotlin Standard Library** - For language features
- **Java 9+** - For Cleaner API (memory management)
