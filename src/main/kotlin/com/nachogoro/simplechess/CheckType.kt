package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * The different types of check which can be caused by a move.
 */
public enum class CheckType {
    NONE, CHECK, CHECKMATE;

    public companion object {
        /**
         * Creates a CheckType from JNA constant.
         */
        internal fun fromJna(jnaCheck: Int): CheckType = when (jnaCheck) {
            ChessLibraryJNA.CheckType.NONE -> NONE
            ChessLibraryJNA.CheckType.CHECK -> CHECK
            ChessLibraryJNA.CheckType.CHECKMATE -> CHECKMATE
            else -> throw IllegalArgumentException("Invalid JNA check type: $jnaCheck")
        }
    }
}