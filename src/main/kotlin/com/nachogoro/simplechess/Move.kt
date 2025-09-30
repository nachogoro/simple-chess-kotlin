package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * Describes a move which can be made by a player.
 */
@ConsistentCopyVisibility
public data class Move private constructor(
    val piece: Piece,
    val from: Square,
    val to: Square,
    val promotion: PieceType? = null
) {

    /**
     * Whether this move involves a pawn promotion.
     */
    public val isPromotion: Boolean get() = promotion != null

    public companion object {
        /**
         * Creates a regular move (non-promotion).
         */
        public fun regularMove(piece: Piece, from: Square, to: Square): Move {
            return Move(piece, from, to, null)
        }

        /**
         * Creates a pawn promotion move.
         *
         * @throws IllegalArgumentException if piece is not a pawn or promotion type is invalid
         */
        public fun pawnPromotion(piece: Piece, from: Square, to: Square, promotionType: PieceType): Move {
            require(piece.type == PieceType.PAWN) { "Only pawns can be promoted, got: ${piece.type}" }
            require(promotionType in setOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                "Invalid promotion type: $promotionType"
            }
            return Move(piece, from, to, promotionType)
        }

        /**
         * Creates a Move from JNA PieceMove.
         */
        internal fun fromJna(jnaMove: ChessLibraryJNA.PieceMove): Move {
            val piece = Piece.fromJna(jnaMove.piece)
            val from = Square.fromJna(jnaMove.src)
            val to = Square.fromJna(jnaMove.dst)
            val promotion = if (jnaMove.is_promotion != 0.toByte()) {
                PieceType.fromJna(jnaMove.promoted_to)
            } else null

            return Move(piece, from, to, promotion)
        }
    }

    /**
     * Converts to JNA PieceMove.ByValue.
     */
    internal fun toJna(): ChessLibraryJNA.PieceMove.ByValue {
        val jnaMove = ChessLibraryJNA.PieceMove.ByValue()
        jnaMove.piece = piece.toJna()
        jnaMove.src = from.toJna()
        jnaMove.dst = to.toJna()
        jnaMove.is_promotion = if (isPromotion) 1 else 0
        jnaMove.promoted_to = promotion?.toJna() ?: 0
        return jnaMove
    }
}

/**
 * Describes a move that has been played in a game of chess.
 */
@ConsistentCopyVisibility
public data class PlayedMove internal constructor(
    val move: Move,
    val isCapture: Boolean,
    val capturedPiece: Piece?,
    val checkType: CheckType,
    val offersDraw: Boolean,
    val algebraicNotation: String
) {

    /**
     * Whether this move resulted in check.
     */
    public val isCheck: Boolean get() = checkType == CheckType.CHECK

    /**
     * Whether this move resulted in checkmate.
     */
    public val isCheckmate: Boolean get() = checkType == CheckType.CHECKMATE

    public companion object {
        /**
         * Creates a PlayedMove from JNA PlayedMove.
         */
        internal fun fromJna(jnaPlayedMove: ChessLibraryJNA.PlayedMove): PlayedMove {
            val move = Move.fromJna(jnaPlayedMove.move)
            val isCapture = jnaPlayedMove.is_capture != 0.toByte()
            val capturedPiece = if (isCapture) Piece.fromJna(jnaPlayedMove.captured_piece) else null
            val checkType = CheckType.fromJna(jnaPlayedMove.check_type)
            val offersDraw = jnaPlayedMove.offers_draw != 0.toByte()
            val algebraicNotation = ChessLibraryJNA.byteArrayToString(jnaPlayedMove.in_algebraic_notation)

            return PlayedMove(move, isCapture, capturedPiece, checkType, offersDraw, algebraicNotation)
        }
    }
}