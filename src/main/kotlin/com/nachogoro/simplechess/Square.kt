package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * Represents a square on the chess board.
 * Squares are identified by rank (1-8) and file ('a'-'h').
 */
@JvmInline
public value class Square private constructor(private val notation: String) {

    /**
     * The file (column) of the square ('a'-'h').
     */
    public val file: Char get() = notation[0]

    /**
     * The rank (row) of the square (1-8).
     */
    public val rank: Int get() = notation[1].digitToInt()

    /**
     * The color of the square (light or dark).
     */
    public val color: Color get() = if ((file.code - 'a'.code + rank) % 2 == 0) Color.BLACK else Color.WHITE

    /**
     * String representation in algebraic notation (e.g., "e4").
     */
    public override fun toString(): String = notation

    public companion object {
        /**
         * Creates a Square from rank and file.
         *
         * @param rank The rank (1-8)
         * @param file The file ('a'-'h')
         * @throws IllegalArgumentException if rank or file are invalid
         */
        public fun fromRankAndFile(rank: Int, file: Char): Square {
            require(rank in 1..8) { "Rank must be between 1 and 8, got: $rank" }
            require(file.lowercaseChar() in 'a'..'h') { "File must be between 'a' and 'h', got: $file" }
            return Square("${file.lowercaseChar()}$rank")
        }

        /**
         * Creates a Square from algebraic notation string.
         *
         * @param notation String in format "e4", "a1", etc.
         * @throws IllegalArgumentException if notation is invalid
         */
        public fun fromString(notation: String): Square {
            require(notation.length == 2) { "Square notation must be exactly 2 characters, got: '$notation'" }
            require(notation.matches(Regex("[a-hA-H][1-8]"))) { "Invalid square notation: '$notation'" }
            return Square(notation.lowercase())
        }

        /**
         * Checks if the given rank and file are within board boundaries.
         */
        public fun isInsideBoundaries(rank: Int, file: Char): Boolean {
            return rank in 1..8 && file.lowercaseChar() in 'a'..'h'
        }

        /**
         * All 64 squares on the chess board.
         */
        public val ALL_SQUARES: List<Square> by lazy {
            buildList {
                for (rank in 1..8) {
                    for (file in 'a'..'h') {
                        add(fromRankAndFile(rank, file))
                    }
                }
            }
        }

        /**
         * Creates a Square from JNA Square.
         */
        internal fun fromJna(jnaSquare: ChessLibraryJNA.Square): Square {
            return fromRankAndFile(jnaSquare.rank.toInt(), jnaSquare.file.toInt().toChar())
        }
    }

    /**
     * Converts to JNA Square.ByValue.
     */
    internal fun toJna(): ChessLibraryJNA.Square.ByValue {
        return ChessLibraryJNA.Square.ByValue(rank, file)
    }
}