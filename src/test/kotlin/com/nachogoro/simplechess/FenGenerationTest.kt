package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.square
import com.nachogoro.simplechess.TestUtils.regularMove
import com.nachogoro.simplechess.TestUtils.promotionMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class FenGenerationTest {

    @Test
    fun blackMoveNoCapture() {
        val gameResult = Game.fromFen("rnbqkbnr/ppp2ppp/8/3pp1B1/4P3/3P4/PPP2PPP/RN1QKBNR b KQkq - 3 5")
        val game = assertSuccess(gameResult)

        val move = regularMove(
            piece(PieceType.QUEEN, Color.BLACK),
            square(8, 'd'),
            square(6, 'f')
        )

        val resultGame = assertSuccess(game.makeMove(move))

        assertEquals(
            "rnb1kbnr/ppp2ppp/5q2/3pp1B1/4P3/3P4/PPP2PPP/RN1QKBNR w KQkq - 4 6",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun blackMoveWithCapture() {
        val gameResult = Game.fromFen("8/pB1K4/7N/8/1RnP1P1q/4P3/4k3/8 b - - 15 26")
        val game = assertSuccess(gameResult)

        val move = regularMove(
            piece(PieceType.QUEEN, Color.BLACK),
            square(4, 'h'),
            square(6, 'h')
        )

        val resultGame = assertSuccess(game.makeMove(move))

        assertEquals(
            "8/pB1K4/7q/8/1RnP1P2/4P3/4k3/8 w - - 0 27",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun blackPawnPromotion() {
        val gameResult = Game.fromFen("8/1B1K4/7N/8/1RnP1P1q/4P3/p3k3/8 b - - 15 26")
        val game = assertSuccess(gameResult)

        val move = promotionMove(
            piece(PieceType.PAWN, Color.BLACK),
            square(2, 'a'),
            square(1, 'a'),
            PieceType.QUEEN
        )

        val resultGame = assertSuccess(game.makeMove(move))

        assertEquals(
            "8/1B1K4/7N/8/1RnP1P1q/4P3/4k3/q7 w - - 0 27",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun whiteCastlingKingside() {
        val gameResult = Game.fromFen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1")
        val game = assertSuccess(gameResult)

        val castlingMove = regularMove(
            piece(PieceType.KING, Color.WHITE),
            square(1, 'e'),
            square(1, 'g')
        )

        val resultGame = assertSuccess(game.makeMove(castlingMove))

        assertEquals(
            "r3k2r/8/8/8/8/8/8/R4RK1 b kq - 1 1",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun whiteCastlingQueenside() {
        val gameResult = Game.fromFen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1")
        val game = assertSuccess(gameResult)

        val castlingMove = regularMove(
            piece(PieceType.KING, Color.WHITE),
            square(1, 'e'),
            square(1, 'c')
        )

        val resultGame = assertSuccess(game.makeMove(castlingMove))

        assertEquals(
            "r3k2r/8/8/8/8/8/8/2KR3R b kq - 1 1",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun enPassantCapture() {
        val gameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 3")
        val game = assertSuccess(gameResult)

        val enPassantMove = regularMove(
            piece(PieceType.PAWN, Color.WHITE),
            square(5, 'f'),
            square(6, 'e')
        )

        val resultGame = assertSuccess(game.makeMove(enPassantMove))

        assertEquals(
            "rnbqkbnr/pppp1ppp/4P3/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun pawnDoubleMove() {
        val gameResult = Game.newGame()
        val game = assertSuccess(gameResult)

        val pawnDoubleMove = regularMove(
            piece(PieceType.PAWN, Color.WHITE),
            square(2, 'e'),
            square(4, 'e')
        )

        val resultGame = assertSuccess(game.makeMove(pawnDoubleMove))

        assertEquals(
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun whiteMoveNoCapture() {
        val gameResult = Game.fromFen("rnbqkbnr/ppp2ppp/8/3pp1B1/4P3/3P4/PPP2PPP/RN1QKBNR w KQkq - 3 5")
        val game = assertSuccess(gameResult)

        val move = regularMove(
            piece(PieceType.KNIGHT, Color.WHITE),
            square(1, 'b'),
            square(3, 'c')
        )

        val resultGame = assertSuccess(game.makeMove(move))

        assertEquals(
            "rnbqkbnr/ppp2ppp/8/3pp1B1/4P3/2NP4/PPP2PPP/R2QKBNR b KQkq - 4 5",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun whiteMoveWithCapture() {
        val gameResult = Game.fromFen("8/pB1K4/7N/8/1RnP1P1q/4P3/4k3/8 w - - 1 30")
        val game = assertSuccess(gameResult)

        val move = regularMove(
            piece(PieceType.ROOK, Color.WHITE),
            square(4, 'b'),
            square(4, 'c')
        )

        val resultGame = assertSuccess(game.makeMove(move))

        assertEquals(
            "8/pB1K4/7N/8/2RP1P1q/4P3/4k3/8 b - - 0 30",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun whitePawnMove() {
        val gameResult = Game.fromFen("8/pB1K4/7N/8/1RnP1P1q/4P3/4k3/8 w - - 12 29")
        val game = assertSuccess(gameResult)

        val move = regularMove(
            piece(PieceType.PAWN, Color.WHITE),
            square(4, 'f'),
            square(5, 'f')
        )

        val resultGame = assertSuccess(game.makeMove(move))

        assertEquals(
            "8/pB1K4/7N/5P2/1RnP3q/4P3/4k3/8 b - - 0 29",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun enPassantTaken() {
        val gameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/8/3Pp3/2N5/PPP1PPPP/R1BQKBNR b KQkq d3 0 1")
        val game = assertSuccess(gameResult)

        val enPassantMove = regularMove(
            piece(PieceType.PAWN, Color.BLACK),
            square(4, 'e'),
            square(3, 'd')
        )

        val resultGame = assertSuccess(game.makeMove(enPassantMove))

        assertEquals(
            "rnbqkbnr/pppp1ppp/8/8/8/2Np4/PPP1PPPP/R1BQKBNR w KQkq - 0 2",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun enPassantIgnored() {
        val gameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/8/3Pp3/2N5/PPP1PPPP/R1BQKBNR b KQkq d3 0 1")
        val game = assertSuccess(gameResult)

        val regularPawnMove = regularMove(
            piece(PieceType.PAWN, Color.BLACK),
            square(4, 'e'),
            square(3, 'e')
        )

        val resultGame = assertSuccess(game.makeMove(regularPawnMove))

        assertEquals(
            "rnbqkbnr/pppp1ppp/8/8/3P4/2N1p3/PPP1PPPP/R1BQKBNR w KQkq - 0 2",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun enPassantIsCreated() {
        val gameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/4p3/8/4P3/PPPP1PPP/RNBQKBNR w KQkq e6 0 2")
        val game = assertSuccess(gameResult)

        val pawnDoubleMove = regularMove(
            piece(PieceType.PAWN, Color.WHITE),
            square(2, 'f'),
            square(4, 'f')
        )

        val resultGame = assertSuccess(game.makeMove(pawnDoubleMove))

        assertEquals(
            "rnbqkbnr/pppp1ppp/8/4p3/5P2/4P3/PPPP2PP/RNBQKBNR b KQkq f3 0 2",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun blackKingsideCastling() {
        val gameResult = Game.fromFen("3rk2r/8/8/8/8/8/8/3RK2R b Kk - 0 41")
        val game = assertSuccess(gameResult)

        val castlingMove = regularMove(
            piece(PieceType.KING, Color.BLACK),
            square(8, 'e'),
            square(8, 'g')
        )

        val resultGame = assertSuccess(game.makeMove(castlingMove))

        assertEquals(
            "3r1rk1/8/8/8/8/8/8/3RK2R w K - 1 42",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun blackQueensideCastling() {
        val gameResult = Game.fromFen("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 7 52")
        val game = assertSuccess(gameResult)

        val castlingMove = regularMove(
            piece(PieceType.KING, Color.BLACK),
            square(8, 'e'),
            square(8, 'c')
        )

        val resultGame = assertSuccess(game.makeMove(castlingMove))

        assertEquals(
            "2kr3r/8/8/8/8/8/8/R3K2R w KQ - 8 53",
            resultGame.currentPosition.fen
        )
    }

    @Test
    fun moveCounterAndActiveColor() {
        val gameResult = Game.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
        val game = assertSuccess(gameResult)

        val blackMove = regularMove(
            piece(PieceType.PAWN, Color.BLACK),
            square(7, 'e'),
            square(5, 'e')
        )

        val resultGame = assertSuccess(game.makeMove(blackMove))

        // After black's move, it should be white's turn and full move counter should increment
        assertEquals(
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2",
            resultGame.currentPosition.fen
        )
    }
}