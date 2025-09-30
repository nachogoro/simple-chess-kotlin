package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertError
import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.square
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class GameCreationTest {

    @Test
    fun regularGameCreation() {
        val gameResult = Game.newGame()
        val game = assertSuccess(gameResult)

        assertEquals(GameState.PLAYING, game.gameState)

        // drawReason should be null when game is not drawn
        assertNull(game.drawReason)

        val history = game.history
        assertEquals(0, history.size)
        assertEquals(Color.WHITE, game.activeColor)
        assertNull(game.drawClaimReason)

        // Validate the starting position FEN
        val currentPos = game.currentPosition
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", currentPos.fen)
    }

    @Test
    fun gameCreationFromPosition1() {
        val gameResult = Game.fromFen("5rk1/3Q1p1p/6p1/8/3B4/4K3/8/8 b - - 0 1")
        val game = assertSuccess(gameResult)

        assertEquals(GameState.PLAYING, game.gameState)

        // drawReason should be null when game is not drawn
        assertNull(game.drawReason)

        val history = game.history
        assertEquals(0, history.size)
        assertEquals(Color.BLACK, game.activeColor)
        assertNull(game.drawClaimReason)

        // Validate the FEN is preserved
        val currentPos = game.currentPosition
        assertEquals("5rk1/3Q1p1p/6p1/8/3B4/4K3/8/8 b - - 0 1", currentPos.fen)
    }

    @Test
    fun gameCreationFromPositionInCheckmate() {
        val gameResult = Game.fromFen("6kr/5Q1p/3N2p1/8/8/4K3/8/8 b - - 0 1")
        val game = assertSuccess(gameResult)

        assertEquals(GameState.WHITE_WON, game.gameState)
    }

    @Test
    fun gameCreationFromPositionInStalemate() {
        val gameResult = Game.fromFen("7k/5Qr1/5Q2/5B2/8/4K3/8/8 b - - 0 1")
        val game = assertSuccess(gameResult)

        assertEquals(GameState.DRAWN, game.gameState)
        assertEquals(DrawReason.STALEMATE, game.drawReason)
    }

    @Test
    fun gameCreationWithTooManyKings() {
        val gameResult = Game.fromFen("5kk1/5Qr1/5Q2/5B2/8/4K3/8/8 b - - 0 1")
        assertError(gameResult)
    }

    @Test
    fun gameCreationWithTooFewKings() {
        val gameResult = Game.fromFen("8/5Qr1/5Q2/5B2/8/4K3/8/8 b - - 0 1")
        assertError(gameResult)
    }

    @Test
    fun gameCreationActiveSideAlreadyChecking() {
        val gameResult = Game.fromFen("k4n2/5n1K/8/8/8/8/8/6r1 b - - 0 1")
        assertError(gameResult)
    }
}