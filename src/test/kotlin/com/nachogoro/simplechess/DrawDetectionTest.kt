package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertError
import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.square
import com.nachogoro.simplechess.TestUtils.regularMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class DrawDetectionTest {

    @Test
    fun offerDraw() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        assertNull(startingGame.drawClaimReason)

        val knightMove = regularMove(
            piece(PieceType.KNIGHT, Color.WHITE),
            square(1, 'g'),
            square(3, 'f')
        )

        // Offer draw
        val updatedResult = startingGame.makeMove(knightMove, offerDraw = true)
        val updated = assertSuccess(updatedResult)

        assertEquals(GameState.PLAYING, updated.gameState)
        assertNotNull(updated.drawClaimReason)
        assertEquals(DrawReason.OFFERED_AND_ACCEPTED, updated.drawClaimReason)
    }

    @Test
    fun offerDrawAndAccept() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        val knightMove = regularMove(
            piece(PieceType.KNIGHT, Color.WHITE),
            square(1, 'g'),
            square(3, 'f')
        )

        // Offer draw
        val updatedResult = startingGame.makeMove(knightMove, offerDraw = true)
        val updated = assertSuccess(updatedResult)

        // Accept draw
        val drawnGameResult = updated.claimDraw()
        val drawnGame = assertSuccess(drawnGameResult)

        assertEquals(GameState.DRAWN, drawnGame.gameState)
        assertEquals(DrawReason.OFFERED_AND_ACCEPTED, drawnGame.drawReason)
    }

    @Test
    fun offerDrawAndReject() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        val whiteMove = regularMove(
            piece(PieceType.KNIGHT, Color.WHITE),
            square(1, 'g'),
            square(3, 'f')
        )

        val blackMove = regularMove(
            piece(PieceType.KNIGHT, Color.BLACK),
            square(8, 'g'),
            square(6, 'f')
        )

        // White offers draw
        val withDrawOfferResult = startingGame.makeMove(whiteMove, offerDraw = true)
        val withDrawOffer = assertSuccess(withDrawOfferResult)
        assertNotNull(withDrawOffer.drawClaimReason)
        assertEquals(DrawReason.OFFERED_AND_ACCEPTED, withDrawOffer.drawClaimReason)

        // Black rejects draw by making a normal move
        val withDrawRejectedResult = withDrawOffer.makeMove(blackMove, offerDraw = false)
        val withDrawRejected = assertSuccess(withDrawRejectedResult)
        assertEquals(GameState.PLAYING, withDrawRejected.gameState)
        assertNull(withDrawRejected.drawClaimReason)
    }

    @Test
    fun stalemate() {
        val startingGameResult = Game.fromFen("8/5b2/1q6/3R3r/2K1N3/2P5/4k3/8 b - - 0 1")
        val startingGame = assertSuccess(startingGameResult)
        assertNull(startingGame.drawClaimReason)

        val causeStaleMate = regularMove(
            piece(PieceType.ROOK, Color.BLACK),
            square(5, 'h'),
            square(4, 'h')
        )

        val updatedResult = startingGame.makeMove(causeStaleMate, offerDraw = false)
        val updated = assertSuccess(updatedResult)
        assertEquals(GameState.DRAWN, updated.gameState)
        assertEquals(DrawReason.STALEMATE, updated.drawReason)
    }

    @Test
    fun insufficientMaterialKingvsKing() {
        val startingGameResult = Game.fromFen("8/3k4/8/4p3/3K4/8/8/8 w - - 0 1")
        val startingGame = assertSuccess(startingGameResult)
        assertEquals(GameState.PLAYING, startingGame.gameState)

        val pawnCapture = regularMove(
            piece(PieceType.KING, Color.WHITE),
            square(4, 'd'),
            square(5, 'e')
        )

        val withNoMaterial = assertSuccess(startingGame.makeMove(pawnCapture))
        assertEquals(GameState.DRAWN, withNoMaterial.gameState)
        assertEquals(DrawReason.INSUFFICIENT_MATERIAL, withNoMaterial.drawReason)
    }

    @Test
    fun insufficientMaterialKingvsKingAndBishop() {
        val startingGameResult = Game.fromFen("3k4/4R3/2B5/8/3K4/8/8/8 b - - 0 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)

        val rookCapture = regularMove(
            piece(PieceType.KING, Color.BLACK),
            square(8, 'd'),
            square(7, 'e')
        )

        val withNoMaterial = assertSuccess(startingGame.makeMove(rookCapture))
        assertEquals(GameState.DRAWN, withNoMaterial.gameState)
        assertEquals(DrawReason.INSUFFICIENT_MATERIAL, withNoMaterial.drawReason)
    }

    @Test
    fun insufficientMaterialKingvsKingAndKnight() {
        val startingGameResult = Game.fromFen("3k4/3R4/2N5/8/3K4/8/8/8 b - - 0 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)

        val rookCapture = regularMove(
            piece(PieceType.KING, Color.BLACK),
            square(8, 'd'),
            square(7, 'd')
        )

        val withNoMaterial = assertSuccess(startingGame.makeMove(rookCapture))
        assertEquals(GameState.DRAWN, withNoMaterial.gameState)
        assertEquals(DrawReason.INSUFFICIENT_MATERIAL, withNoMaterial.drawReason)
    }

    @Test
    fun insufficientMaterialKingAndBishopvsKingAndSameColoredBishop() {
        val startingGameResult = Game.fromFen("3k4/2b5/8/3r4/3K4/8/8/6B1 w - - 0 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)

        val rookCapture = regularMove(
            piece(PieceType.KING, Color.WHITE),
            square(4, 'd'),
            square(5, 'd')
        )

        val withNoMaterial = assertSuccess(startingGame.makeMove(rookCapture))
        assertEquals(GameState.DRAWN, withNoMaterial.gameState)
        assertEquals(DrawReason.INSUFFICIENT_MATERIAL, withNoMaterial.drawReason)
    }

    @Test
    fun insufficientMaterialKingAndBishopvsKingAndOppositeColorBishop() {
        val startingGameResult = Game.fromFen("3k4/2b5/8/3r4/3K4/8/8/7B w - - 0 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)

        val rookCapture = regularMove(
            piece(PieceType.KING, Color.WHITE),
            square(4, 'd'),
            square(5, 'd')
        )

        val stillEnoughMaterial = assertSuccess(startingGame.makeMove(rookCapture))
        assertEquals(GameState.PLAYING, stillEnoughMaterial.gameState)
    }

    @Test
    fun invalidClaim() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)
        val claimResult = startingGame.claimDraw()
        assertError(claimResult)
    }

    @Test
    fun nFoldRepetition() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        val whiteKnightForward = regularMove(
            piece(PieceType.KNIGHT, Color.WHITE),
            square(1, 'g'),
            square(3, 'f')
        )

        val whiteKnightBack = regularMove(
            piece(PieceType.KNIGHT, Color.WHITE),
            square(3, 'f'),
            square(1, 'g')
        )

        val blackKnightForward = regularMove(
            piece(PieceType.KNIGHT, Color.BLACK),
            square(8, 'g'),
            square(6, 'f')
        )

        val blackKnightBack = regularMove(
            piece(PieceType.KNIGHT, Color.BLACK),
            square(6, 'f'),
            square(8, 'g')
        )

        val move1 = assertSuccess(startingGame.makeMove(whiteKnightForward))
        assertNull(move1.drawClaimReason)
        val move1Black = assertSuccess(move1.makeMove(blackKnightForward))
        assertNull(move1Black.drawClaimReason)
        val move2 = assertSuccess(move1Black.makeMove(whiteKnightBack))
        assertNull(move2.drawClaimReason)
        val move2Black = assertSuccess(move2.makeMove(blackKnightBack))
        assertNull(move2Black.drawClaimReason)

        val move3 = assertSuccess(move2Black.makeMove(whiteKnightForward))
        assertNull(move3.drawClaimReason)
        val move3Black = assertSuccess(move3.makeMove(blackKnightForward))
        assertNull(move3Black.drawClaimReason)
        val move4 = assertSuccess(move3Black.makeMove(whiteKnightBack))
        // At this point, black could play blackKnightBack, which would cause
        // 3-fold repetition. Therefore, black can claim a draw at this point, even
        // before making the move.
        assertNotNull(move4.drawClaimReason)

        // Continue to create 5-fold repetition which should be automatic
        val move4Black = assertSuccess(move4.makeMove(blackKnightBack))
        val move5 = assertSuccess(move4Black.makeMove(whiteKnightForward))
        val move5Black = assertSuccess(move5.makeMove(blackKnightForward))
        val move6 = assertSuccess(move5Black.makeMove(whiteKnightBack))
        val move6Black = assertSuccess(move6.makeMove(blackKnightBack))
        val move7 = assertSuccess(move6Black.makeMove(whiteKnightForward))
        val move7Black = assertSuccess(move7.makeMove(blackKnightForward))
        val move8 = assertSuccess(move7Black.makeMove(whiteKnightBack))
        val fiveFold = assertSuccess(move8.makeMove(blackKnightBack))

        assertEquals(GameState.DRAWN, fiveFold.gameState)
        assertEquals(DrawReason.FIVE_FOLD_REPETITION, fiveFold.drawReason)
    }

    @Test
    fun fiftyMoveRule() {
        val startingGameResult = Game.fromFen("3k4/2b5/8/3r4/8/8/3K4/7B w - - 98 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)
        assertNull(startingGame.drawClaimReason)

        // Available when the move would be the 50th
        val oneBeforeFifty = assertSuccess(startingGame.makeMove(
            regularMove(
                piece(PieceType.KING, Color.WHITE),
                square(2, 'd'),
                square(2, 'c')
            )
        ))

        assertEquals(GameState.PLAYING, oneBeforeFifty.gameState)
        assertNotNull(oneBeforeFifty.drawClaimReason)
        assertEquals(DrawReason.FIFTY_MOVE_RULE, oneBeforeFifty.drawClaimReason)

        // Test not automatically claimed at fifty
        val exactlyFifty = assertSuccess(oneBeforeFifty.makeMove(
            regularMove(
                piece(PieceType.BISHOP, Color.BLACK),
                square(7, 'c'),
                square(6, 'b')
            )
        ))

        assertEquals(GameState.PLAYING, exactlyFifty.gameState)
        assertNotNull(exactlyFifty.drawClaimReason)
        assertEquals(DrawReason.FIFTY_MOVE_RULE, exactlyFifty.drawClaimReason)

        // Not automatically claimed after fifty
        val afterFifty = assertSuccess(exactlyFifty.makeMove(
            regularMove(
                piece(PieceType.BISHOP, Color.WHITE),
                square(1, 'h'),
                square(2, 'g')
            )
        ))

        assertEquals(GameState.PLAYING, afterFifty.gameState)
        assertNotNull(afterFifty.drawClaimReason)
        assertEquals(DrawReason.FIFTY_MOVE_RULE, afterFifty.drawClaimReason)
    }

    @Test
    fun seventyFiveMoveRule() {
        val startingGameResult = Game.fromFen("3k4/2b5/8/3r4/8/8/3K4/7B w - - 149 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)
        assertNotNull(startingGame.drawClaimReason)
        assertEquals(DrawReason.FIFTY_MOVE_RULE, startingGame.drawClaimReason)

        val seventyFiveFullMoves = assertSuccess(startingGame.makeMove(
            regularMove(
                piece(PieceType.KING, Color.WHITE),
                square(2, 'd'),
                square(2, 'c')
            )
        ))

        assertEquals(GameState.DRAWN, seventyFiveFullMoves.gameState)
        assertEquals(DrawReason.SEVENTY_FIVE_MOVE_RULE, seventyFiveFullMoves.drawReason)
    }

    @Test
    fun seventyFiveMoveRuleCheckmateOverride() {
        // Position where black can deliver checkmate on the 75th move
        val startingGameResult = Game.fromFen("1r3k2/8/8/8/8/8/4PPPP/6K1 b - - 149 1")
        val startingGame = assertSuccess(startingGameResult)

        assertEquals(GameState.PLAYING, startingGame.gameState)
        assertNotNull(startingGame.drawClaimReason)
        assertEquals(DrawReason.FIFTY_MOVE_RULE, startingGame.drawClaimReason)

        // The 75th move delivers checkmate - checkmate should take precedence over draw
        val checkmateOn75th = assertSuccess(startingGame.makeMove(
            regularMove(
                piece(PieceType.ROOK, Color.BLACK),
                square(8, 'b'),
                square(1, 'b')
            )
        ))

        // Checkmate takes precedence over the 75-move rule
        assertEquals(GameState.BLACK_WON, checkmateOn75th.gameState)
    }
}