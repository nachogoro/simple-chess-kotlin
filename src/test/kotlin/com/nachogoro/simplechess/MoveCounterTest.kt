package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.square
import com.nachogoro.simplechess.TestUtils.regularMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MoveCounterTest {

    @Test
    fun fullMoveCounterFromStart() {
        val gameResult = Game.newGame()
        val game = assertSuccess(gameResult)
        assertEquals(1, game.currentPosition.fullMoveNumber)

        val afterFirstWhiteMoveResult = game.makeMove(
            Move.regularMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(2, 'e'),
                square(4, 'e')
            )
        )
        val afterFirstWhiteMove = assertSuccess(afterFirstWhiteMoveResult)
        assertEquals(1, afterFirstWhiteMove.currentPosition.fullMoveNumber)

        val afterBlackFirstMoveResult = afterFirstWhiteMove.makeMove(
            Move.regularMove(
                piece(PieceType.PAWN, Color.BLACK),
                square(7, 'e'),
                square(5, 'e')
            )
        )
        val afterBlackFirstMove = assertSuccess(afterBlackFirstMoveResult)
        assertEquals(2, afterBlackFirstMove.currentPosition.fullMoveNumber)

        val afterWhiteResponseResult = afterBlackFirstMove.makeMove(
            Move.regularMove(
                piece(PieceType.KNIGHT, Color.WHITE),
                square(1, 'g'),
                square(3, 'f')
            )
        )
        val afterWhiteResponse = assertSuccess(afterWhiteResponseResult)
        assertEquals(2, afterWhiteResponse.currentPosition.fullMoveNumber)
    }

    @Test
    fun fullMoveCounterFromFenStartingWhite() {
        val gameResult = Game.fromFen("8/4k3/6p1/2n5/7P/2RB4/1K6/8 w - - 0 63")
        val game = assertSuccess(gameResult)
        assertEquals(63, game.currentPosition.fullMoveNumber)

        val afterWhiteMoveResult = game.makeMove(
            Move.regularMove(
                piece(PieceType.ROOK, Color.WHITE),
                square(3, 'c'),
                square(5, 'c')
            )
        )
        val afterWhiteMove = assertSuccess(afterWhiteMoveResult)
        assertEquals(63, afterWhiteMove.currentPosition.fullMoveNumber)

        val afterBlackResponseResult = afterWhiteMove.makeMove(
            Move.regularMove(
                piece(PieceType.PAWN, Color.BLACK),
                square(6, 'g'),
                square(5, 'g')
            )
        )
        val afterBlackResponse = assertSuccess(afterBlackResponseResult)
        assertEquals(64, afterBlackResponse.currentPosition.fullMoveNumber)

        val afterWhiteNextMoveResult = afterBlackResponse.makeMove(
            Move.regularMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(4, 'h'),
                square(5, 'g')
            )
        )
        val afterWhiteNextMove = assertSuccess(afterWhiteNextMoveResult)
        assertEquals(64, afterWhiteNextMove.currentPosition.fullMoveNumber)
    }

    @Test
    fun fullMoveCounterFromFenStartingBlack() {
        val gameResult = Game.fromFen("8/4k3/6p1/2n5/7P/2RB4/1K6/8 b - - 0 51")
        val game = assertSuccess(gameResult)
        assertEquals(51, game.currentPosition.fullMoveNumber)

        val afterBlackMoveResult = game.makeMove(
            Move.regularMove(
                piece(PieceType.KNIGHT, Color.BLACK),
                square(5, 'c'),
                square(3, 'd')
            )
        )
        val afterBlackMove = assertSuccess(afterBlackMoveResult)
        assertEquals(52, afterBlackMove.currentPosition.fullMoveNumber)

        val afterWhiteResponseResult = afterBlackMove.makeMove(
            Move.regularMove(
                piece(PieceType.KING, Color.WHITE),
                square(2, 'b'),
                square(3, 'b')
            )
        )
        val afterWhiteResponse = assertSuccess(afterWhiteResponseResult)
        assertEquals(52, afterWhiteResponse.currentPosition.fullMoveNumber)

        val afterBlackNextMoveResult = afterWhiteResponse.makeMove(
            Move.regularMove(
                piece(PieceType.PAWN, Color.BLACK),
                square(6, 'g'),
                square(5, 'g')
            )
        )
        val afterBlackNextMove = assertSuccess(afterBlackNextMoveResult)
        assertEquals(53, afterBlackNextMove.currentPosition.fullMoveNumber)
    }

    @Test
    fun halfMoveCounter() {
        val gameResult = Game.newGame()
        val game = assertSuccess(gameResult)
        assertEquals(0, game.currentPosition.halfMoveClock)

        // Pawn move does not increase the counter if it is 0
        val afterWhite1Result = game.makeMove(
            Move.regularMove(
                piece(PieceType.PAWN, Color.WHITE),
                square(2, 'e'),
                square(4, 'e')
            )
        )
        val afterWhite1 = assertSuccess(afterWhite1Result)
        assertEquals(0, afterWhite1.currentPosition.halfMoveClock)

        // Non capture or pawn advance moves increase the counter by 1
        val afterBlack1Result = afterWhite1.makeMove(
            Move.regularMove(
                piece(PieceType.KNIGHT, Color.BLACK),
                square(8, 'g'),
                square(6, 'f')
            )
        )
        val afterBlack1 = assertSuccess(afterBlack1Result)
        assertEquals(1, afterBlack1.currentPosition.halfMoveClock)

        val afterWhite2Result = afterBlack1.makeMove(
            Move.regularMove(
                piece(PieceType.KNIGHT, Color.WHITE),
                square(1, 'b'),
                square(3, 'c')
            )
        )
        val afterWhite2 = assertSuccess(afterWhite2Result)
        assertEquals(2, afterWhite2.currentPosition.halfMoveClock)

        // Captures reset the counter to 0
        val afterBlack2Result = afterWhite2.makeMove(
            Move.regularMove(
                piece(PieceType.KNIGHT, Color.BLACK),
                square(6, 'f'),
                square(4, 'e')
            )
        )
        val afterBlack2 = assertSuccess(afterBlack2Result)
        assertEquals(0, afterBlack2.currentPosition.halfMoveClock)

        val afterWhite3Result = afterBlack2.makeMove(
            Move.regularMove(
                piece(PieceType.QUEEN, Color.WHITE),
                square(1, 'd'),
                square(4, 'g')
            )
        )
        val afterWhite3 = assertSuccess(afterWhite3Result)
        assertEquals(1, afterWhite3.currentPosition.halfMoveClock)

        val afterBlack3Result = afterWhite3.makeMove(
            Move.regularMove(
                piece(PieceType.KNIGHT, Color.BLACK),
                square(8, 'b'),
                square(6, 'c')
            )
        )
        val afterBlack3 = assertSuccess(afterBlack3Result)
        assertEquals(2, afterBlack3.currentPosition.halfMoveClock)
    }
}