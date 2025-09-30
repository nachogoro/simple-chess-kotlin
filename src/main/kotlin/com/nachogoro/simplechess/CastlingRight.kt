package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * Castling rights bitfield values.
 */
public enum class CastlingRight(internal val jnaValue: Int) {
    WHITE_KING_SIDE(ChessLibraryJNA.CastlingRight.WHITE_KING_SIDE),
    WHITE_QUEEN_SIDE(ChessLibraryJNA.CastlingRight.WHITE_QUEEN_SIDE),
    BLACK_KING_SIDE(ChessLibraryJNA.CastlingRight.BLACK_KING_SIDE),
    BLACK_QUEEN_SIDE(ChessLibraryJNA.CastlingRight.BLACK_QUEEN_SIDE);

    public companion object {
        /**
         * Creates a set of CastlingRight from JNA bitfield.
         */
        internal fun fromJnaBitfield(bitfield: Int): Set<CastlingRight> {
            val rights = mutableSetOf<CastlingRight>()
            for (right in entries) {
                if ((bitfield and right.jnaValue) != 0) {
                    rights.add(right)
                }
            }
            return rights
        }

        /**
         * Converts a set of CastlingRight to JNA bitfield.
         */
        internal fun toJnaBitfield(rights: Set<CastlingRight>): Int {
            return rights.fold(0) { acc, right -> acc or right.jnaValue }
        }
    }
}