package com.nachogoro.simplechess

import com.nachogoro.simplechess.internal.ChessLibraryJNA
import com.sun.jna.Pointer
import java.lang.ref.Cleaner

/**
 * A representation of a chess game at a given point.
 *
 * The interface is immutable - all methods that would change state
 * return a new Game instance with the updated state.
 *
 * Users cannot create Game instances directly - use the factory methods
 * in the companion object.
 */
public sealed interface Game {
    /**
     * The current state of the game.
     */
    public val gameState: GameState

    /**
     * The reason why the game ended in a draw (only valid if gameState is DRAWN).
     */
    public val drawReason: DrawReason?

    /**
     * The history of the game as pairs of position and move.
     */
    public val history: List<Pair<Position, PlayedMove>>

    /**
     * The current position of the game.
     */
    public val currentPosition: Position

    /**
     * The color which is to move next.
     */
    public val activeColor: Color get() = currentPosition.activeColor

    /**
     * All available moves for the player whose turn it is to play.
     */
    public val availableMoves: List<Move>

    /**
     * Returns an optional reason under which the current player can claim a draw.
     * Returns null if a draw cannot be claimed.
     */
    public val drawClaimReason: DrawReason?

    /**
     * Whether a draw can be claimed by the current player.
     */
    public val canClaimDraw: Boolean get() = drawClaimReason != null

    /**
     * Returns all possible moves for a given piece on the board.
     * If the square is empty or does not contain a piece of the active player,
     * an empty list is returned.
     */
    public fun availableMovesForPiece(square: Square): List<Move>

    /**
     * Make a move for the player whose turn it is to play.
     *
     * @param move The desired move
     * @param offerDraw true if the move is accompanied by an offer to draw, false otherwise
     * @return A ChessResult containing a new Game with the move applied, or an Error if:
     *         - The Game has already concluded (state is not PLAYING)
     *         - The move is not valid for the current player
     */
    public fun makeMove(move: Move, offerDraw: Boolean = false): ChessResult<Game>

    /**
     * Claim a draw if one is available.
     *
     * @return A ChessResult containing a new Game finished as a draw, or an Error if:
     *         - The Game has already concluded (state is not PLAYING)
     *         - The current player cannot claim a draw
     */
    public fun claimDraw(): ChessResult<Game>

    /**
     * Resign the game for the specified player.
     *
     * @param resigningPlayer The color of the player who is resigning
     * @return A ChessResult containing a new Game finished with resignation, or an Error if:
     *         - The Game has already concluded (state is not PLAYING)
     */
    public fun resign(resigningPlayer: Color): ChessResult<Game>

    public companion object {
        /**
         * Factory method to create a new game from the standard starting position.
         *
         * @return A ChessResult containing the new Game, or an Error if creation fails
         */
        public fun newGame(): ChessResult<Game> = GameImpl.createNewGame()

        /**
         * Factory method to create a new game from a given board position.
         *
         * The original position of the board is given as a string in
         * Forsyth-Edwards Notation (FEN).
         *
         * Note: FEN descriptions only give limited information about the
         * history of the game. In particular, one cannot enforce certain
         * drawing rules (threefold repetition). Hence, the history of the
         * resulting Game will not necessarily be complete.
         *
         * @param fen The representation of the initial position in Forsyth-Edwards Notation
         * @return A ChessResult containing the new Game, or an Error if the FEN is invalid
         */
        public fun fromFen(fen: String): ChessResult<Game> = GameImpl.createFromFen(fen)
    }
}

/**
 * Internal implementation of the Game interface.
 * Uses the Cleaner API for automatic memory management of native resources.
 */
internal class GameImpl private constructor(
    private val nativePtr: Pointer
) : Game {

    @Volatile
    private var destroyed = false

    private val gameData: ChessLibraryJNA.Game by lazy {
        ChessLibraryJNA.getGameFromPointer(nativePtr)
            ?: throw IllegalStateException("Failed to read game data from native pointer")
    }

    override val gameState: GameState by lazy {
        GameState.fromJna(gameData.state)
    }

    override val drawReason: DrawReason? by lazy {
        if (gameState == GameState.DRAWN) {
            DrawReason.fromJna(gameData.draw_reason)
        } else null
    }

    override val history: List<Pair<Position, PlayedMove>> by lazy {
        if (gameData.history_size > 0) {
            val historyEntries = ChessLibraryJNA.getHistoryFromPointer(gameData.history, gameData.history_size.toInt())
            historyEntries.map { entry ->
                val position = Position.fromJna(GameStageFromFen(ChessLibraryJNA.byteArrayToString(entry.fen)))
                val playedMove = PlayedMove.fromJna(entry.played_move)
                position to playedMove
            }
        } else emptyList()
    }

    override val currentPosition: Position by lazy {
        Position.fromJna(gameData.current_stage)
    }

    override val availableMoves: List<Move> by lazy {
        if (gameData.available_move_count > 0) {
            val jnaMoves = ChessLibraryJNA.getAvailableMovesFromPointer(
                gameData.available_moves,
                gameData.available_move_count.toInt()
            )
            jnaMoves.map { Move.fromJna(it) }
        } else emptyList()
    }

    override val drawClaimReason: DrawReason? by lazy {
        if (gameData.is_draw_claimable != 0.toByte()) {
            DrawReason.fromJna(gameData.reason_to_claim_draw)
        } else null
    }

    override fun availableMovesForPiece(square: Square): List<Move> {
        // Filter available moves to only those starting from the given square
        return availableMoves.filter { it.from == square }
    }

    override fun makeMove(move: Move, offerDraw: Boolean): ChessResult<Game> {
        return ChessResult.catching {
            validateGamePlaying()
            val jnaMove = move.toJna()
            val newPtr = if (offerDraw) {
                ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_make_move_with_draw_offer(nativePtr, jnaMove, true)
            } else {
                ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_make_move(nativePtr, jnaMove)
            }

            if (newPtr == null) {
                throw IllegalArgumentException("Invalid move: $move")
            }

            create(newPtr)
        }
    }

    override fun claimDraw(): ChessResult<Game> {
        return ChessResult.catching {
            validateGamePlaying()
            if (!canClaimDraw) {
                throw IllegalStateException("No draw can be claimed in the current position")
            }

            val newPtr = ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_claim_draw(nativePtr)
                ?: throw IllegalStateException("Failed to claim draw")

            create(newPtr)
        }
    }

    override fun resign(resigningPlayer: Color): ChessResult<Game> {
        return ChessResult.catching {
            validateGamePlaying()
            val newPtr = ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_resign(nativePtr, resigningPlayer.toJna())
                ?: throw IllegalStateException("Failed to resign game")

            create(newPtr)
        }
    }

    private fun validateGamePlaying() {
        if (gameState != GameState.PLAYING) {
            throw IllegalStateException("Game has already concluded with state: $gameState")
        }
    }

    companion object {
        private val cleaner = Cleaner.create()

        internal fun createNewGame(): ChessResult<Game> {
            return ChessResult.catching {
                val ptr = ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_create_new_game()
                    ?: throw RuntimeException("Failed to create new game - native library returned null")
                create(ptr)
            }
        }

        fun createFromFen(fen: String): ChessResult<Game> {
            return ChessResult.catching {
                val ptr = ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_create_game_from_fen(fen)
                    ?: throw IllegalArgumentException("Invalid FEN string: $fen")
                create(ptr)
            }
        }

        /**
         * Creates a GameImpl instance with automatic cleanup.
         */
        internal fun create(nativePtr: Pointer): GameImpl {
            val game = GameImpl(nativePtr)
            // Register cleanup action that captures only the pointer, avoiding circular references
            cleaner.register(game, CleanupAction(nativePtr))
            return game
        }

        /**
         * Cleanup action that only holds the native pointer to avoid memory leaks.
         */
        private class CleanupAction(private val ptr: Pointer) : Runnable {
            override fun run() {
                try {
                    ChessLibraryJNA.ChessLib.INSTANCE.destroy_game(ptr)
                } catch (e: Exception) {
                    // Log but don't throw - cleanup should be silent
                    System.err.println("Warning: Failed to cleanup native game resource: ${e.message}")
                }
            }
        }
    }

    /**
     * Helper function to create a GameStage from FEN.
     */
    private fun GameStageFromFen(fen: String): ChessLibraryJNA.GameStage {
        val tempPtr = ChessLibraryJNA.ChessLib.INSTANCE.simple_chess_create_game_from_fen(fen)
        if (tempPtr != null) {
            try {
                val tempGame = ChessLibraryJNA.getGameFromPointer(tempPtr)
                return tempGame?.current_stage ?: throw IllegalArgumentException("Invalid FEN: $fen")
            } finally {
                ChessLibraryJNA.ChessLib.INSTANCE.destroy_game(tempPtr)
            }
        } else {
            throw IllegalArgumentException("Invalid FEN: $fen")
        }
    }
}