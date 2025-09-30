package com.nachogoro.simplechess

import org.junit.jupiter.api.Assertions.*

/**
 * Test utilities for chess library tests.
 */
object TestUtils {
    /**
     * Asserts that a ChessResult is an Error.
     */
    fun <T> assertError(result: ChessResult<T>) {
        assertTrue(result.isError, "Expected Error but got Success: ${result.getOrNull()}")
    }

    /**
     * Asserts that a ChessResult is a Success and returns the value.
     */
    fun <T> assertSuccess(result: ChessResult<T>): T {
        assertTrue(result.isSuccess, "Expected Success but got Error: ${(result as? ChessResult.Error)?.message}")
        return result.getOrThrow()
    }

    /**
     * Creates a piece for testing.
     */
    fun piece(type: PieceType, color: Color): Piece = Piece.create(type, color)

    /**
     * Creates a square for testing.
     */
    fun square(rank: Int, file: Char): Square = Square.fromRankAndFile(rank, file)

    /**
     * Creates a regular move for testing.
     */
    fun regularMove(game: Game, from: Square, to: Square): Move =
        Move.regularMove(game.currentPosition.board[from]!!, from, to)

    /**
     * Creates a regular move for testing.
     */
    fun regularMove(piece: Piece, from: Square, to: Square): Move =
        Move.regularMove(piece, from, to)

    /**
     * Creates a pawn promotion move for testing.
     */
    fun promotionMove(game: Game, from: Square, to: Square, promotionType: PieceType): Move =
        Move.pawnPromotion(game.currentPosition.board[from]!!, from, to, promotionType)

    /**
     * Creates a pawn promotion move for testing.
     */
    fun promotionMove(piece: Piece, from: Square, to: Square, promotionType: PieceType): Move =
        Move.pawnPromotion(piece, from, to, promotionType)
}