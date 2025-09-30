package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.square
import com.nachogoro.simplechess.TestUtils.regularMove
import com.nachogoro.simplechess.TestUtils.promotionMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class AlgebraicNotationTest {

    @Test
    fun pieceMoveNoCaptureNoCheckNoAmbiguity() {
        val gameResult = Game.fromFen("r1bqkb1r/pppppppp/2n5/8/4n1Q1/2N5/PPPP1PPP/R1B1KBNR w KQkq - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.KNIGHT, Color.WHITE),
                square(3, 'c'),
                square(5, 'b')
            )
        ))

        assertEquals("Nb5", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveCaptureNoCheckNoAmbiguity() {
        val gameResult = Game.fromFen("r1bqkb1r/pppppppp/2n5/8/2n1P1Q1/2N5/PPP2PPP/R1B1KBNR w KQkq - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.BISHOP, Color.WHITE),
                square(1, 'f'),
                square(4, 'c')
            )
        ))

        assertEquals("Bxc4", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveNoCaptureCheckNoAmbiguity() {
        val gameResult = Game.fromFen("q1q5/q4k2/2P5/3r4/2P1B3/5K2/Q7/8 b - - 1 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.QUEEN, Color.BLACK),
                square(8, 'c'),
                square(3, 'h')
            )
        ))

        assertEquals("Qh3+", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveCaptureCheckNoAmbiguity() {
        val gameResult = Game.fromFen("q7/1P3k2/8/3r4/2P1B2q/5K2/Q7/8 b - - 1 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.QUEEN, Color.BLACK),
                square(4, 'h'),
                square(4, 'e')
            )
        ))

        assertEquals("Qxe4+", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveNoCaptureCheckMateNoAmbiguity() {
        val gameResult = Game.fromFen("4k3/R6R/8/8/8/8/8/4K3 w - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.ROOK, Color.WHITE),
                square(7, 'h'),
                square(8, 'h')
            )
        ))

        assertEquals("Rh8#", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveNoCaptureNoCheckSameRankAmbiguity() {
        val gameResult = Game.fromFen("8/4k3/8/8/8/6K1/8/R6R w - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.ROOK, Color.WHITE),
                square(1, 'h'),
                square(1, 'd')
            )
        ))

        assertEquals("Rhd1", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveNoCaptureNoCheckSameRankNoAmbiguity() {
        val gameResult = Game.fromFen("8/4k3/8/8/8/6K1/8/R6R w - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.ROOK, Color.WHITE),
                square(1, 'h'),
                square(2, 'h')
            )
        ))

        assertEquals("Rh2", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveCaptureNoCheckSameFileAmbiguity() {
        val gameResult = Game.fromFen("b4k2/8/2P5/8/b7/8/8/5K2 b - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.BISHOP, Color.BLACK),
                square(8, 'a'),
                square(6, 'c')
            )
        ))

        assertEquals("B8xc6", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveCaptureCheckSameFileSameRankAmbiguity() {
        val gameResult = Game.fromFen("b3bk2/8/2P5/8/b7/5K2/8/8 b - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.BISHOP, Color.BLACK),
                square(8, 'a'),
                square(6, 'c')
            )
        ))

        assertEquals("Ba8xc6+", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pawnPromotionNoCaptureNoCheck() {
        val gameResult = Game.fromFen("2rk4/1P6/8/5K2/8/8/8/8 w - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            promotionMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(7, 'b'),
                square(8, 'b'),
                PieceType.QUEEN
            )
        ))

        assertEquals("b8=Q", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pawnPromotionCaptureCheck() {
        val gameResult = Game.fromFen("2rk4/1P6/8/5K2/8/8/8/8 w - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            promotionMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(7, 'b'),
                square(8, 'c'),
                PieceType.ROOK
            )
        ))

        assertEquals("bxc8=R+", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pawnRegularMoveCaptureAmbiguityNoCheck() {
        val gameResult = Game.fromFen("k7/8/8/3p1p2/4N3/8/8/7K b - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.PAWN, Color.BLACK),
                square(5, 'd'),
                square(4, 'e')
            )
        ))

        assertEquals("dxe4", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pawnRegularMoveCaptureNoAmbiguityNoCheck() {
        val gameResult = Game.fromFen("k7/8/8/6pp/7N/8/8/7K b - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.PAWN, Color.BLACK),
                square(5, 'g'),
                square(4, 'h')
            )
        ))

        assertEquals("gxh4", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pawnEnPassantCaptureNoAmbiguityNoCheck() {
        val gameResult = Game.fromFen("7k/8/8/Pp6/8/7K/8/8 w - b6 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(5, 'a'),
                square(6, 'b')
            )
        ))

        assertEquals("axb6", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pawnEnPassantCaptureAmbiguityNoCheck() {
        val gameResult = Game.fromFen("7k/8/8/PpP5/8/7K/8/8 w - b6 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(5, 'a'),
                square(6, 'b')
            )
        ))

        assertEquals("axb6", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun pieceMoveNoCaptureCheckNoAmbiguityDrawOffer() {
        val gameResult = Game.fromFen("8/8/3K4/8/Q7/8/p7/1k6 w - - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.QUEEN, Color.WHITE),
                square(4, 'a'),
                square(4, 'b')
            ),
            offerDraw = true
        ))

        assertEquals("Qb4+(=)", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun castlingKingsideNoCheck() {
        val gameResult = Game.fromFen("8/8/8/8/6k1/8/4PP1P/4K2R w K - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.KING, Color.WHITE),
                square(1, 'e'),
                square(1, 'g')
            )
        ))

        assertEquals("O-O", updatedGame.history.last().second.algebraicNotation)
    }

    @Test
    fun castlingQueensideCheckmate() {
        val gameResult = Game.fromFen("r3k1K1/1q6/8/8/8/8/8/8 b q - 0 1")
        val game = assertSuccess(gameResult)

        val updatedGame = assertSuccess(game.makeMove(
            regularMove(
                piece(PieceType.KING, Color.BLACK),
                square(8, 'e'),
                square(8, 'c')
            )
        ))

        assertEquals("O-O-O#", updatedGame.history.last().second.algebraicNotation)
    }
}