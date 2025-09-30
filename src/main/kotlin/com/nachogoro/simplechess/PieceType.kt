package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * The type of a chess piece.
 */
public enum class PieceType {
    PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING;

    /**
     * Converts to the JNA constant.
     */
    internal fun toJna(): Int = when (this) {
        PAWN -> ChessLibraryJNA.PieceType.PAWN
        ROOK -> ChessLibraryJNA.PieceType.ROOK
        KNIGHT -> ChessLibraryJNA.PieceType.KNIGHT
        BISHOP -> ChessLibraryJNA.PieceType.BISHOP
        QUEEN -> ChessLibraryJNA.PieceType.QUEEN
        KING -> ChessLibraryJNA.PieceType.KING
    }

    public companion object {
        /**
         * Creates a PieceType from JNA constant.
         */
        internal fun fromJna(jnaType: Int): PieceType = when (jnaType) {
            ChessLibraryJNA.PieceType.PAWN -> PAWN
            ChessLibraryJNA.PieceType.ROOK -> ROOK
            ChessLibraryJNA.PieceType.KNIGHT -> KNIGHT
            ChessLibraryJNA.PieceType.BISHOP -> BISHOP
            ChessLibraryJNA.PieceType.QUEEN -> QUEEN
            ChessLibraryJNA.PieceType.KING -> KING
            else -> throw IllegalArgumentException("Invalid JNA piece type: $jnaType")
        }
    }
}