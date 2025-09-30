package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA

/**
 * Represents a chess piece with its type and color.
 */
@JvmInline
public value class Piece private constructor(private val encoded: Int) {

    /**
     * The type of the piece.
     */
    public val type: PieceType get() = PieceType.entries[encoded and 0x7]

    /**
     * The color of the piece.
     */
    public val color: Color get() = if ((encoded shr 3) == 0) Color.WHITE else Color.BLACK

    /**
     * String representation (e.g., "white pawn", "black king").
     */
    public override fun toString(): String = "${color.name.lowercase()} ${type.name.lowercase()}"

    public companion object {
        /**
         * Creates a Piece from type and color.
         */
        public fun create(type: PieceType, color: Color): Piece {
            val colorBit = if (color == Color.WHITE) 0 else 1
            return Piece((colorBit shl 3) or type.ordinal)
        }

        /**
         * Creates a Piece from JNA Piece.
         */
        internal fun fromJna(jnaPiece: ChessLibraryJNA.Piece): Piece {
            return create(PieceType.fromJna(jnaPiece.type), Color.fromJna(jnaPiece.color))
        }
    }

    /**
     * Converts to JNA Piece.ByValue.
     */
    internal fun toJna(): ChessLibraryJNA.Piece.ByValue {
        return ChessLibraryJNA.Piece.ByValue(type.toJna(), color.toJna())
    }
}