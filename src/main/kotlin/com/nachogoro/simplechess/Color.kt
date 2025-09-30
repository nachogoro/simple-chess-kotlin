package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * The color of each side in a chess game.
 */
public enum class Color {
    WHITE, BLACK;

    /**
     * Returns the opposite color.
     */
    public fun opposite(): Color = when (this) {
        WHITE -> BLACK
        BLACK -> WHITE
    }

    /**
     * Converts to the JNA constant.
     */
    internal fun toJna(): Int = when (this) {
        WHITE -> ChessLibraryJNA.Color.WHITE
        BLACK -> ChessLibraryJNA.Color.BLACK
    }

    public companion object {
        /**
         * Creates a Color from JNA constant.
         */
        internal fun fromJna(jnaColor: Int): Color = when (jnaColor) {
            ChessLibraryJNA.Color.WHITE -> WHITE
            ChessLibraryJNA.Color.BLACK -> BLACK
            else -> throw IllegalArgumentException("Invalid JNA color: $jnaColor")
        }
    }
}