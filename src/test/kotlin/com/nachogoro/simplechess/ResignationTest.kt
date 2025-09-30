package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class ResignationTest {

    @Test
    fun whiteResignInTheirTurn() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        val resignedResult = startingGame.resign(Color.WHITE)
        val resigned = assertSuccess(resignedResult)

        assertEquals(GameState.BLACK_WON, resigned.gameState)
    }

    @Test
    fun whiteResignInBlackTurn() {
        val startingGameResult = Game.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
        val startingGame = assertSuccess(startingGameResult)

        val resignedResult = startingGame.resign(Color.WHITE)
        val resigned = assertSuccess(resignedResult)

        assertEquals(GameState.BLACK_WON, resigned.gameState)
    }

    @Test
    fun blackResignInWhiteTurn() {
        val startingGameResult = Game.newGame()
        val startingGame = assertSuccess(startingGameResult)

        val resignedResult = startingGame.resign(Color.BLACK)
        val resigned = assertSuccess(resignedResult)

        assertEquals(GameState.WHITE_WON, resigned.gameState)
    }

    @Test
    fun blackResignInTheirTurn() {
        val startingGameResult = Game.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")
        val startingGame = assertSuccess(startingGameResult)

        val resignedResult = startingGame.resign(Color.BLACK)
        val resigned = assertSuccess(resignedResult)

        assertEquals(GameState.WHITE_WON, resigned.gameState)
    }
}