package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * Represents a complete chess game position.
 */
@ConsistentCopyVisibility
public data class Position internal constructor(
    val fen: String,
    val activeColor: Color,
    val castlingRights: Set<CastlingRight>,
    val enPassantTarget: Square?,
    val halfMoveClock: Int,
    val fullMoveNumber: Int,
    val checkStatus: CheckType,
    val board: Map<Square, Piece>
) {

    /**
     * Whether the active player is in check.
     */
    public val isInCheck: Boolean get() = checkStatus == CheckType.CHECK

    /**
     * Whether the active player is in checkmate.
     */
    public val isInCheckmate: Boolean get() = checkStatus == CheckType.CHECKMATE

    public companion object {
        /**
         * Creates a Position from JNA GameStage.
         */
        internal fun fromJna(jnaStage: ChessLibraryJNA.GameStage): Position {
            val fen = ChessLibraryJNA.byteArrayToString(jnaStage.fen)
            val activeColor = Color.fromJna(jnaStage.active_color)
            val castlingRights = CastlingRight.fromJnaBitfield(jnaStage.castling_rights.toInt())
            val enPassantTarget = if (jnaStage.has_en_passant_target != 0.toByte()) {
                Square.fromJna(jnaStage.en_passant_target)
            } else null
            val halfMoveClock = jnaStage.half_moves_since_last_capture_or_pawn_advance.toInt()
            val fullMoveNumber = jnaStage.full_moves.toInt()
            val checkStatus = CheckType.fromJna(jnaStage.check_status)

            // Convert JNA board to Kotlin map
            val board = buildMap<Square, Piece> {
                for (i in 0 until 64) {
                    if (jnaStage.board.occupied[i] != 0.toByte()) {
                        val rank = (i / 8) + 1
                        val file = ('a' + (i % 8))
                        val square = Square.fromRankAndFile(rank, file)
                        val piece = Piece.fromJna(jnaStage.board.piece_at[i])
                        put(square, piece)
                    }
                }
            }

            return Position(fen, activeColor, castlingRights, enPassantTarget, halfMoveClock, fullMoveNumber, checkStatus, board)
        }
    }
}