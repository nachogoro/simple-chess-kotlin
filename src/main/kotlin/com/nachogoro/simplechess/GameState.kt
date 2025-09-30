package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * The overall state of a chess game.
 */
public enum class GameState {
    PLAYING, DRAWN, WHITE_WON, BLACK_WON;

    public companion object {
        /**
         * Creates a GameState from JNA constant.
         */
        internal fun fromJna(jnaState: Int): GameState = when (jnaState) {
            ChessLibraryJNA.GameState.PLAYING -> PLAYING
            ChessLibraryJNA.GameState.DRAWN -> DRAWN
            ChessLibraryJNA.GameState.WHITE_WON -> WHITE_WON
            ChessLibraryJNA.GameState.BLACK_WON -> BLACK_WON
            else -> throw IllegalArgumentException("Invalid JNA game state: $jnaState")
        }
    }
}