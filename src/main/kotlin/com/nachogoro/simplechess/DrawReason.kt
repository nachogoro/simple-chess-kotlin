package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * Reasons why a game might be drawn.
 */
public enum class DrawReason {
    STALEMATE,
    INSUFFICIENT_MATERIAL,
    OFFERED_AND_ACCEPTED,
    THREE_FOLD_REPETITION,
    FIVE_FOLD_REPETITION,
    FIFTY_MOVE_RULE,
    SEVENTY_FIVE_MOVE_RULE;

    public companion object {
        /**
         * Creates a DrawReason from JNA constant.
         */
        internal fun fromJna(jnaReason: Int): DrawReason = when (jnaReason) {
            ChessLibraryJNA.DrawReason.STALEMATE -> STALEMATE
            ChessLibraryJNA.DrawReason.INSUFFICIENT_MATERIAL -> INSUFFICIENT_MATERIAL
            ChessLibraryJNA.DrawReason.OFFERED_AND_ACCEPTED -> OFFERED_AND_ACCEPTED
            ChessLibraryJNA.DrawReason.THREE_FOLD_REPETITION -> THREE_FOLD_REPETITION
            ChessLibraryJNA.DrawReason.FIVE_FOLD_REPETITION -> FIVE_FOLD_REPETITION
            ChessLibraryJNA.DrawReason.FIFTY_MOVE_RULE -> FIFTY_MOVE_RULE
            ChessLibraryJNA.DrawReason.SEVENTY_FIVE_MOVE_RULE -> SEVENTY_FIVE_MOVE_RULE
            else -> throw IllegalArgumentException("Invalid JNA draw reason: $jnaReason")
        }
    }
}