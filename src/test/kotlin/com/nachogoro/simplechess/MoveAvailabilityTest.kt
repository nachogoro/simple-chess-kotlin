package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.promotionMove
import com.nachogoro.simplechess.TestUtils.square
import com.nachogoro.simplechess.TestUtils.regularMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MoveAvailabilityTest {

    @Test
    fun regularGameMoves() {
        val gameResult = Game.newGame()
        val game = assertSuccess(gameResult)

        val expectedAvailableMoves = setOf(
            // Pawn moves
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'a'), square(3, 'a')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'a'), square(4, 'a')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'b'), square(3, 'b')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'b'), square(4, 'b')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'c'), square(3, 'c')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'c'), square(4, 'c')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'd'), square(3, 'd')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'd'), square(4, 'd')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'e'), square(3, 'e')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'e'), square(4, 'e')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'f'), square(3, 'f')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'f'), square(4, 'f')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'g'), square(3, 'g')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'g'), square(4, 'g')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'h'), square(3, 'h')),
            regularMove(piece(PieceType.PAWN, Color.WHITE), square(2, 'h'), square(4, 'h')),

            // Knight moves
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(1, 'b'), square(3, 'a')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(1, 'b'), square(3, 'c')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(1, 'g'), square(3, 'f')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(1, 'g'), square(3, 'h'))
        )

        val availableMoves = game.availableMoves.toSet()
        assertEquals(expectedAvailableMoves.size, availableMoves.size)
        assertEquals(expectedAvailableMoves, availableMoves)
    }

    @Test
    fun knightMovesUnobstructed() {
        val gameResult = Game.fromFen("7k/8/8/8/3N4/8/8/K7 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedAvailableMoves = setOf(
            // King moves
            regularMove(piece(PieceType.KING, Color.WHITE), square(1, 'a'), square(2, 'a')),
            regularMove(piece(PieceType.KING, Color.WHITE), square(1, 'a'), square(2, 'b')),
            regularMove(piece(PieceType.KING, Color.WHITE), square(1, 'a'), square(1, 'b')),

            // Knight moves (all 8 directions)
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(6, 'c')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(6, 'e')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(5, 'b')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(5, 'f')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(3, 'b')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(3, 'f')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(2, 'c')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(2, 'e'))
        )

        val availableMoves = game.availableMoves.toSet()
        assertEquals(expectedAvailableMoves.size, availableMoves.size)
        assertEquals(expectedAvailableMoves, availableMoves)
    }

    @Test
    fun pawnPromotionMoves() {
        val gameResult = Game.fromFen("2k1n3/5Pb1/8/3p4/8/K7/8/8 w - - 0 1")
        val game = assertSuccess(gameResult)

        val promotionableTypes = listOf(PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN)
        val pawnSquare = square(7, 'f')

        val expectedPawnMoves = promotionableTypes
            .map{ type ->
                listOf(
                    promotionMove(game, pawnSquare, square(8, 'f'), type),
                    promotionMove(game, pawnSquare, square(8, 'e'), type))
            }.flatten()
            .toSet()

        val actualPawnMoves = game.availableMoves.filter{ move -> move.from == pawnSquare }.toSet()
        assertEquals(expectedPawnMoves, actualPawnMoves)
    }

    @Test
    fun enPassantMoves() {
        // Test en passant capture - Black just moved pawn from e7 to e5
        val gameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 3")
        val game = assertSuccess(gameResult)

        // Look for en passant move
        val enPassantMoves = game.availableMoves.filter {
            it.piece.type == PieceType.PAWN &&
                    it.from == square(5, 'f') &&
                    it.to == square(6, 'e')
        }
        assertEquals(1, enPassantMoves.size, "Should have exactly one en passant move")
    }

    @Test
    fun castlingMoves() {
        // Position where both white castling moves are possible
        val gameResult = Game.fromFen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1")
        val game = assertSuccess(gameResult)

        // Look for castling moves (king moves 2 squares)
        val castlingMoves = game.availableMoves.filter {
            it.piece.type == PieceType.KING &&
                    it.piece.color == Color.WHITE &&
                    kotlin.math.abs(it.from.file.code - it.to.file.code) == 2
        }
        assertEquals(2, castlingMoves.size, "Should have exactly two castling moves")
    }

    @Test
    fun noCastlingThroughCheck() {
        // Position where castling is blocked by check
        val gameResult = Game.fromFen("r3k2r/8/8/8/8/8/4r3/R3K2R w KQkq - 0 1")
        val game = assertSuccess(gameResult)

        // Look for castling moves - should be none because castling through check
        val castlingMoves = game.availableMoves.filter {
            it.piece.type == PieceType.KING &&
                    it.piece.color == Color.WHITE &&
                    kotlin.math.abs(it.from.file.code - it.to.file.code) == 2
        }
        assertEquals(0, castlingMoves.size, "Should have no castling moves when castling through check")
    }

    @Test
    fun knightMovesObstructedAttempt() {
        // Knight surrounded by pieces - knight should not be affected by obstruction
        val gameResult = Game.fromFen("7k/8/8/2rrr3/2rNr3/2rrr3/8/K7 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedAvailableMoves = setOf(
            // King moves
            regularMove(game, square(1, 'a'), square(2, 'a')),
            regularMove(game, square(1, 'a'), square(2, 'b')),
            regularMove(game, square(1, 'a'), square(1, 'b')),

            // Knight moves (all 8 directions - unaffected by surrounding pieces)
            regularMove(game, square(4, 'd'), square(6, 'c')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(6, 'e')),
            regularMove(piece(PieceType.KNIGHT, Color.WHITE), square(4, 'd'), square(5, 'b')),
            regularMove(game, square(4, 'd'), square(5, 'f')),
            regularMove(game, square(4, 'd'), square(3, 'b')),
            regularMove(game, square(4, 'd'), square(3, 'f')),
            regularMove(game, square(4, 'd'), square(2, 'c')),
            regularMove(game, square(4, 'd'), square(2, 'e'))
        )

        val availableMoves = game.availableMoves.toSet()
        assertEquals(expectedAvailableMoves.size, availableMoves.size)
        assertEquals(expectedAvailableMoves, availableMoves)
    }

    @Test
    fun bishopMovesUnobstructed() {
        val gameResult = Game.fromFen("3k4/8/8/3BB3/8/8/8/3K4 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(
            square(1, 'c'),
            square(2, 'c'),
            square(2, 'd'),
            square(2, 'e'),
            square(1, 'e')
        ).map{ regularMove(game, square(1, 'd'), it) }

        val expectedDarkBishopMoves = listOf(
            square(6, 'c'),
            square(7, 'b'),
            square(8, 'a'),
            square(6, 'e'),
            square(7, 'f'),
            square(8, 'g'),
            square(4, 'c'),
            square(3, 'b'),
            square(2, 'a'),
            square(4, 'e'),
            square(3, 'f'),
            square(2, 'g'),
            square(1, 'h'),
        ).map{ regularMove(game, square(5, 'd'), it) }

        val expectedLightBishopMoves = listOf(
            square(6, 'd'),
            square(7, 'c'),
            square(8, 'b'),
            square(6, 'f'),
            square(7, 'g'),
            square(8, 'h'),
            square(4, 'd'),
            square(3, 'c'),
            square(2, 'b'),
            square(1, 'a'),
            square(4, 'f'),
            square(3, 'g'),
            square(2, 'h'),
        ).map{ regularMove(game, square(5, 'e'), it) }

        val expectedMoves = (expectedKingMoves + expectedDarkBishopMoves + expectedLightBishopMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun bishopMovesObstructed() {
        val gameResult = Game.fromFen("7k/r5r1/3r4/8/1r1B2r1/8/1r3r2/2K5 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(regularMove(game, square(1, 'c'), square(1, 'd')))
        val expectedBishopMoves = listOf(
            square(5, 'c'),
            square(6, 'b'),
            square(7, 'a'),
            square(5, 'e'),
            square(6, 'f'),
            square(7, 'g'),
            square(3, 'c'),
            square(2, 'b'),
            square(3, 'e'),
            square(2, 'f')
        ).map{
            regularMove(game, square(4, 'd'), it)
        }

        val expectedMoves = (expectedKingMoves + expectedBishopMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun rookMovesUnobstructed() {
        val gameResult = Game.fromFen("4k3/8/8/3R4/8/8/8/4K3 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(
            square(1, 'd'),
            square(2, 'd'),
            square(2, 'e'),
            square(1, 'f'),
            square(2, 'f')
        ).map { regularMove(game, square(1, 'e'), it) }

        val expectedRookMoves = listOf(
            // Horizontal moves (along rank 5)
            square(5, 'a'),
            square(5, 'b'),
            square(5, 'c'),
            square(5, 'e'),
            square(5, 'f'),
            square(5, 'g'),
            square(5, 'h'),
            // Vertical moves (along d-file)
            square(8, 'd'),
            square(7, 'd'),
            square(6, 'd'),
            square(4, 'd'),
            square(3, 'd'),
            square(2, 'd'),
            square(1, 'd')
        ).map { regularMove(game, square(5, 'd'), it) }

        val expectedMoves = (expectedKingMoves + expectedRookMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun rookMovesObstructed() {
        val gameResult = Game.fromFen("7k/r5r1/3r4/8/1r1R2r1/8/1r3r2/2K5 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(regularMove(game, square(1, 'c'), square(1, 'd')))

        val expectedRookMoves = listOf(
            // Horizontal moves (along rank 4) - blocked by rooks at b4 and g4
            square(4, 'c'),
            square(4, 'e'),
            square(4, 'f'),
            // Vertical moves (along d-file) - blocked by rook at d6 from above
            square(5, 'd'),
            square(3, 'd'),
            square(2, 'd'),
            square(1, 'd'),
            // Captures
            square(4, 'b'), // capture rook on b4
            square(4, 'g'), // capture rook on g4
            square(6, 'd')  // capture rook on d6
        ).map { regularMove(game, square(4, 'd'), it) }

        val expectedMoves = (expectedKingMoves + expectedRookMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun queenMovesUnobstructed() {
        val gameResult = Game.fromFen("4k3/8/8/3Q4/8/8/8/4K3 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(
            square(1, 'd'),
            square(2, 'd'),
            square(2, 'e'),
            square(1, 'f'),
            square(2, 'f')
        ).map { regularMove(game, square(1, 'e'), it) }

        val expectedQueenMoves = listOf(
            // Horizontal moves (along rank 5)
            square(5, 'a'),
            square(5, 'b'),
            square(5, 'c'),
            square(5, 'e'),
            square(5, 'f'),
            square(5, 'g'),
            square(5, 'h'),
            // Vertical moves (along d-file)
            square(8, 'd'),
            square(7, 'd'),
            square(6, 'd'),
            square(4, 'd'),
            square(3, 'd'),
            square(2, 'd'),
            square(1, 'd'),
            // Diagonal moves (north-west)
            square(6, 'c'),
            square(7, 'b'),
            square(8, 'a'),
            // Diagonal moves (north-east)
            square(6, 'e'),
            square(7, 'f'),
            square(8, 'g'),
            // Diagonal moves (south-west)
            square(4, 'c'),
            square(3, 'b'),
            square(2, 'a'),
            // Diagonal moves (south-east)
            square(4, 'e'),
            square(3, 'f'),
            square(2, 'g'),
            square(1, 'h')
        ).map { regularMove(game, square(5, 'd'), it) }

        val expectedMoves = (expectedKingMoves + expectedQueenMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun queenMovesObstructed() {
        val gameResult = Game.fromFen("7k/r5r1/3r4/8/1r1Q2r1/8/1r3r2/2K5 w - - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(regularMove(game, square(1, 'c'), square(1, 'd')))

        val expectedQueenMoves = listOf(
            // Horizontal moves (along rank 4) - blocked by rooks at b4 and g4
            square(4, 'c'),
            square(4, 'e'),
            square(4, 'f'),
            // Vertical moves (along d-file) - blocked by rook at d6 from above
            square(5, 'd'),
            square(3, 'd'),
            square(2, 'd'),
            square(1, 'd'),
            // Diagonal moves (north-west) - blocked by rook at a7
            square(5, 'c'),
            square(6, 'b'),
            square(7, 'a'),
            // Diagonal moves (north-east) - blocked by rook at g7
            square(5, 'e'),
            square(6, 'f'),
            square(7, 'g'),
            // Diagonal moves (south-west) - blocked by rook at b2
            square(3, 'c'),
            square(2, 'b'),
            // Diagonal moves (south-east) - blocked by rook at f2
            square(3, 'e'),
            square(2, 'f'),
            // Captures
            square(4, 'b'), // capture rook on b4
            square(4, 'g'), // capture rook on g4
            square(6, 'd')  // capture rook on d6
        ).map { regularMove(game, square(4, 'd'), it) }

        val expectedMoves = (expectedKingMoves + expectedQueenMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun whiteCastlingUnobstructed() {
        val gameResult = Game.fromFen("1k6/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(
            square(2, 'e'),
            square(1, 'd'),
            square(2, 'd'),
            square(1, 'f'),
            square(2, 'f'),
            // Castling moves
            square(1, 'c'), // queenside castling
            square(1, 'g')  // kingside castling
        ).map { regularMove(game, square(1, 'e'), it) }

        val expectedRookMoves = listOf(
            // a1 rook moves
            square(1, 'b'),
            square(1, 'c'),
            square(1, 'd'),
            square(2, 'a'),
            square(3, 'a'),
            square(4, 'a'),
            square(5, 'a'),
            square(6, 'a'),
            square(7, 'a'),
            square(8, 'a'),
            // h1 rook moves
            square(1, 'f'),
            square(1, 'g'),
            square(2, 'h'),
            square(3, 'h'),
            square(4, 'h'),
            square(5, 'h'),
            square(6, 'h'),
            square(7, 'h'),
            square(8, 'h')
        ).map { to ->
            val from = if (to.file <= 'd') square(1, 'a') else square(1, 'h')
            regularMove(game, from, to)
        }

        val expectedMoves = (expectedKingMoves + expectedRookMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun whiteCastlingQueensideObstructed() {
        val gameResult = Game.fromFen("1k6/8/8/6b1/8/8/8/R3K2R w KQ - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(
            square(2, 'e'),
            square(1, 'd'),
            square(1, 'f'),
            square(2, 'f'),
            square(1, 'g')
        ).map { regularMove(game, square(1, 'e'), it) }

        val expectedA1RookMoves = listOf(
            square(1, 'b'),
            square(1, 'c'),
            square(1, 'd'),
            square(2, 'a'),
            square(3, 'a'),
            square(4, 'a'),
            square(5, 'a'),
            square(6, 'a'),
            square(7, 'a'),
            square(8, 'a')
        ).map { to ->
            regularMove(game, square(1, 'a'), to)
        }

        val expectedH1RookMoves = listOf(
            square(1, 'f'),
            square(1, 'g'),
            square(2, 'h'),
            square(3, 'h'),
            square(4, 'h'),
            square(5, 'h'),
            square(6, 'h'),
            square(7, 'h'),
            square(8, 'h')
        ).map { to ->
            regularMove(game, square(1, 'h'), to)
        }

        val expectedMoves = (expectedKingMoves + expectedA1RookMoves + expectedH1RookMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun whiteCastlingBothObstructed() {
        val gameResult = Game.fromFen("1k6/8/8/6q1/8/8/8/R3K2R w KQ - 0 1")
        val game = assertSuccess(gameResult)

        val expectedKingMoves = listOf(
            square(2, 'e'),
            square(1, 'd'),
            square(1, 'f'),
            square(2, 'f')
        ).map { regularMove(game, square(1, 'e'), it) }

        val expectedRookMoves = listOf(
            // a1 rook moves
            square(1, 'b'),
            square(1, 'c'),
            square(1, 'd'),
            square(2, 'a'),
            square(3, 'a'),
            square(4, 'a'),
            square(5, 'a'),
            square(6, 'a'),
            square(7, 'a'),
            square(8, 'a'),
            // h1 rook moves
            square(1, 'f'),
            square(1, 'g'),
            square(2, 'h'),
            square(3, 'h'),
            square(4, 'h'),
            square(5, 'h'),
            square(6, 'h'),
            square(7, 'h'),
            square(8, 'h')
        ).map { to ->
            val from = if (to.file <= 'd') square(1, 'a') else square(1, 'h')
            regularMove(game, from, to)
        }

        val expectedMoves = (expectedKingMoves + expectedRookMoves).toSet()
        val actualMoves = game.availableMoves.toSet()
        assertEquals(expectedMoves.size, actualMoves.size)
        assertEquals(expectedMoves, actualMoves)
    }

    @Test
    fun whiteCastlingUnavailable() {
        val gameResult = Game.fromFen("1k6/8/8/8/8/8/8/R3K2R w - - 0 1")
        val game = assertSuccess(gameResult)

        val castlingMoves = game.availableMoves.filter {
            it.piece.type == PieceType.KING &&
                    it.piece.color == Color.WHITE &&
                    kotlin.math.abs(it.from.file.code - it.to.file.code) == 2
        }
        assertEquals(0, castlingMoves.size, "Should have no castling moves when rights are not available")
    }

    @Test
    fun blackCastlingInCheck() {
        val gameResult = Game.fromFen("4k2r/8/8/8/8/2K5/8/4R3 b k - 0 1")
        val game = assertSuccess(gameResult)

        val castlingMoves = game.availableMoves.filter {
            it.piece.type == PieceType.KING &&
                    it.piece.color == Color.BLACK &&
                    kotlin.math.abs(it.from.file.code - it.to.file.code) == 2
        }
        assertEquals(0, castlingMoves.size, "Should have no castling moves when in check")
    }

    @Test
    fun enPassantAvailable() {
        val gameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/8/4pP2/4P3/PPPP2PP/RNBQKBNR b KQkq f3 0 1")
        val game = assertSuccess(gameResult)

        val enPassantMoves = game.availableMoves.filter {
            it.piece.type == PieceType.PAWN &&
                    it.piece.color == Color.BLACK &&
                    it.from == square(4, 'e') &&
                    it.to == square(3, 'f')
        }
        assertEquals(1, enPassantMoves.size, "Should have en passant move available")
    }

    @Test
    fun enPassantWouldLeaveInCheck() {
        val gameResult = Game.fromFen("2k5/6b1/8/3pP3/8/8/1K6/8 w - d6 0 1")
        val game = assertSuccess(gameResult)

        val enPassantMoves = game.availableMoves.filter {
            it.piece.type == PieceType.PAWN &&
                    it.piece.color == Color.WHITE &&
                    it.from == square(5, 'e') &&
                    it.to == square(6, 'd')
        }
        assertEquals(0, enPassantMoves.size, "Should not have en passant move when it would leave king in check")
    }

}