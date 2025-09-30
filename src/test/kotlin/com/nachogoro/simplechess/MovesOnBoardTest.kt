package com.nachogoro.simplechess

import com.nachogoro.simplechess.TestUtils.assertSuccess
import com.nachogoro.simplechess.TestUtils.piece
import com.nachogoro.simplechess.TestUtils.square
import com.nachogoro.simplechess.TestUtils.regularMove
import com.nachogoro.simplechess.TestUtils.promotionMove
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class MovesOnBoardTest {

    enum class Difference {
        PIECE_APPEARED,
        PIECE_DISAPPEARED,
        PIECE_REPLACED
    }

    data class Effect(
        val piece: Piece,
        val diff: Difference
    )

    private fun compareBoards(
        after: Map<Square, Piece>,
        before: Map<Square, Piece>
    ): Map<Square, Effect> {
        val result = mutableMapOf<Square, Effect>()

        // Check for pieces that appeared or were replaced
        for ((square, pieceAfter) in after) {
            val pieceBefore = before[square]
            if (pieceBefore == null) {
                // This square was empty before but not after, a piece appeared
                result[square] = Effect(pieceAfter, Difference.PIECE_APPEARED)
            } else if (pieceBefore != pieceAfter) {
                // The contents of this square have changed
                result[square] = Effect(pieceAfter, Difference.PIECE_REPLACED)
            }
        }

        // Check for pieces that disappeared
        for ((square, pieceBefore) in before) {
            val pieceAfter = after[square]
            if (pieceAfter == null) {
                // This square was occupied before but not after, a piece disappeared
                result[square] = Effect(pieceBefore, Difference.PIECE_DISAPPEARED)
            }
        }

        return result
    }

    private fun regularNonCaptureTest(
        startingGame: Game,
        piece: Piece,
        src: Square,
        dst: Square
    ) {
        val afterMoveResult = startingGame.makeMove(Move.regularMove(piece, src, dst))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            src to Effect(piece, Difference.PIECE_DISAPPEARED),
            dst to Effect(piece, Difference.PIECE_APPEARED)
        )

        assertEquals(expected, comparison)
    }

    private fun regularCaptureTest(
        startingGame: Game,
        piece: Piece,
        src: Square,
        dst: Square
    ) {
        val afterMoveResult = startingGame.makeMove(Move.regularMove(piece, src, dst))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            src to Effect(piece, Difference.PIECE_DISAPPEARED),
            dst to Effect(piece, Difference.PIECE_REPLACED)
        )

        assertEquals(expected, comparison)
    }

    private fun castlingTest(
        startingGame: Game,
        castlingColor: Color,
        kingSrc: Square,
        kingDst: Square,
        rookSrc: Square,
        rookDst: Square
    ) {
        val king = piece(PieceType.KING, castlingColor)
        val rook = piece(PieceType.ROOK, castlingColor)

        val afterMoveResult = startingGame.makeMove(Move.regularMove(king, kingSrc, kingDst))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            kingSrc to Effect(king, Difference.PIECE_DISAPPEARED),
            kingDst to Effect(king, Difference.PIECE_APPEARED),
            rookSrc to Effect(rook, Difference.PIECE_DISAPPEARED),
            rookDst to Effect(rook, Difference.PIECE_APPEARED)
        )

        assertEquals(expected, comparison)
    }

    @Test
    fun pawnOnceForward() {
        regularNonCaptureTest(
            assertSuccess(Game.newGame()),
            piece(PieceType.PAWN, Color.WHITE),
            square(2, 'f'),
            square(3, 'f')
        )
    }

    @Test
    fun pawnTwiceForward() {
        regularNonCaptureTest(
            assertSuccess(Game.newGame()),
            piece(PieceType.PAWN, Color.WHITE),
            square(2, 'a'),
            square(4, 'a')
        )
    }

    @Test
    fun pawnCapture() {
        regularCaptureTest(
            assertSuccess(Game.fromFen("rn1qk2r/ppp2ppp/3p1n2/4p3/3P2b1/2N1P3/PPPBQPPP/R3KBNR b KQkq - 0 1")),
            piece(PieceType.PAWN, Color.BLACK),
            square(5, 'e'),
            square(4, 'd')
        )
    }

    @Test
    fun whitePawnEnPassant() {
        val startingGameResult = Game.fromFen("rnbqkbnr/pppp1ppp/8/3Pp3/8/8/PPP1PPPP/RNBQKBNR w KQkq e6 0 1")
        val startingGame = assertSuccess(startingGameResult)

        val src = square(5, 'd')
        val dst = square(6, 'e')
        val squareOfCapturedPawn = square(5, 'e')

        val piece = piece(PieceType.PAWN, Color.WHITE)

        val afterMoveResult = startingGame.makeMove(Move.regularMove(piece, src, dst))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            src to Effect(piece, Difference.PIECE_DISAPPEARED),
            dst to Effect(piece, Difference.PIECE_APPEARED),
            squareOfCapturedPawn to Effect(piece(PieceType.PAWN, Color.BLACK), Difference.PIECE_DISAPPEARED)
        )

        assertEquals(expected, comparison)
    }

    @Test
    fun blackPawnEnPassant() {
        val startingGameResult = Game.fromFen("8/4k3/8/8/6pP/8/1K6/8 b - h3 0 1")
        val startingGame = assertSuccess(startingGameResult)

        val src = square(4, 'g')
        val dst = square(3, 'h')
        val squareOfCapturedPawn = square(4, 'h')

        val piece = piece(PieceType.PAWN, Color.BLACK)

        val afterMoveResult = startingGame.makeMove(Move.regularMove(piece, src, dst))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            src to Effect(piece, Difference.PIECE_DISAPPEARED),
            dst to Effect(piece, Difference.PIECE_APPEARED),
            squareOfCapturedPawn to Effect(piece(PieceType.PAWN, Color.WHITE), Difference.PIECE_DISAPPEARED)
        )

        assertEquals(expected, comparison)
    }

    @Test
    fun pawnPromotionNoCapture() {
        val startingGameResult = Game.fromFen("8/4k3/8/2q5/7P/2RQ4/1K4p1/8 b - - 0 1")
        val startingGame = assertSuccess(startingGameResult)

        val src = square(2, 'g')
        val dst = square(1, 'g')

        val piece = piece(PieceType.PAWN, Color.BLACK)
        val promoted = piece(PieceType.QUEEN, Color.BLACK)

        val afterMoveResult = startingGame.makeMove(Move.pawnPromotion(piece, src, dst, PieceType.QUEEN))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            src to Effect(piece, Difference.PIECE_DISAPPEARED),
            dst to Effect(promoted, Difference.PIECE_APPEARED)
        )

        assertEquals(expected, comparison)
    }

    @Test
    fun pawnPromotionCapture() {
        val startingGameResult = Game.fromFen("2q5/1P2k3/8/8/8/2RQ4/1K4p1/8 w - - 0 1")
        val startingGame = assertSuccess(startingGameResult)

        val src = square(7, 'b')
        val dst = square(8, 'c')

        val piece = piece(PieceType.PAWN, Color.WHITE)
        val promoted = piece(PieceType.QUEEN, Color.WHITE)

        val afterMoveResult = startingGame.makeMove(Move.pawnPromotion(piece, src, dst, PieceType.QUEEN))
        val afterMove = assertSuccess(afterMoveResult)

        val comparison = compareBoards(
            afterMove.currentPosition.board,
            startingGame.currentPosition.board
        )

        val expected = mapOf(
            src to Effect(piece, Difference.PIECE_DISAPPEARED),
            dst to Effect(promoted, Difference.PIECE_REPLACED)
        )

        assertEquals(expected, comparison)
    }

    @Test
    fun knightNoCapture() {
        regularNonCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/8/2n5/6pP/3B4/1K6/8 b - h3 0 1")),
            piece(PieceType.KNIGHT, Color.BLACK),
            square(5, 'c'),
            square(3, 'b')
        )
    }

    @Test
    fun knightCapture() {
        regularCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/8/2n5/6pP/3B4/1K6/8 b - h3 0 1")),
            piece(PieceType.KNIGHT, Color.BLACK),
            square(5, 'c'),
            square(3, 'd')
        )
    }

    @Test
    fun bishopNoCapture() {
        regularNonCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/6p1/2n5/7P/3B4/1K6/8 w - - 0 1")),
            piece(PieceType.BISHOP, Color.WHITE),
            square(3, 'd'),
            square(1, 'f')
        )
    }

    @Test
    fun bishopCapture() {
        regularCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/6p1/2n5/7P/3B4/1K6/8 w - - 0 1")),
            piece(PieceType.BISHOP, Color.WHITE),
            square(3, 'd'),
            square(6, 'g')
        )
    }

    @Test
    fun rookNoCapture() {
        regularNonCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/6p1/2n5/7P/2RB4/1K6/8 w - - 0 1")),
            piece(PieceType.ROOK, Color.WHITE),
            square(3, 'c'),
            square(4, 'c')
        )
    }

    @Test
    fun rookCapture() {
        regularCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/6p1/2n5/7P/2RB4/1K6/8 w - - 0 1")),
            piece(PieceType.ROOK, Color.WHITE),
            square(3, 'c'),
            square(5, 'c')
        )
    }

    @Test
    fun queenNoCapture() {
        regularNonCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/6p1/2q5/7P/2RQ4/1K6/8 b - - 0 1")),
            piece(PieceType.QUEEN, Color.BLACK),
            square(5, 'c'),
            square(2, 'f')
        )
    }

    @Test
    fun queenCapture() {
        regularCaptureTest(
            assertSuccess(Game.fromFen("8/4k3/6p1/2q5/7P/2RQ4/1K6/8 b - - 0 1")),
            piece(PieceType.QUEEN, Color.BLACK),
            square(5, 'c'),
            square(3, 'c')
        )
    }

    @Test
    fun kingNoCapture() {
        regularNonCaptureTest(
            assertSuccess(Game.fromFen("2k5/1P6/8/8/8/2RQ4/1K4p1/8 b - - 0 1")),
            piece(PieceType.KING, Color.BLACK),
            square(8, 'c'),
            square(8, 'b')
        )
    }

    @Test
    fun kingCapture() {
        regularCaptureTest(
            assertSuccess(Game.fromFen("2k5/1P6/8/8/8/2RQ4/1K4p1/8 b - - 0 1")),
            piece(PieceType.KING, Color.BLACK),
            square(8, 'c'),
            square(7, 'b')
        )
    }

    @Test
    fun kingsideCastlingWhite() {
        castlingTest(
            assertSuccess(Game.fromFen("r2qkbnr/ppp2ppp/2np4/1B2p3/6b1/4PN2/PPPP1PPP/RNBQK2R w KQkq - 0 1")),
            Color.WHITE,
            square(1, 'e'),
            square(1, 'g'),
            square(1, 'h'),
            square(1, 'f')
        )
    }

    @Test
    fun queensideCastlingWhite() {
        castlingTest(
            assertSuccess(Game.fromFen("r2qkbnr/ppp2ppp/2np4/4p3/6b1/2NPP3/PPPBQPPP/R3KBNR w KQkq - 0 1")),
            Color.WHITE,
            square(1, 'e'),
            square(1, 'c'),
            square(1, 'a'),
            square(1, 'd')
        )
    }

    @Test
    fun kingsideCastlingBlack() {
        castlingTest(
            assertSuccess(Game.fromFen("rn1qk2r/ppp2ppp/3p1n2/4p3/6b1/2NPP3/PPPBQPPP/R3KBNR b KQkq - 0 1")),
            Color.BLACK,
            square(8, 'e'),
            square(8, 'g'),
            square(8, 'h'),
            square(8, 'f')
        )
    }

    @Test
    fun queensideCastlingBlack() {
        castlingTest(
            assertSuccess(Game.fromFen("r3kbnr/ppp2ppp/2np4/4p1q1/6b1/2NPP3/PPPBQPPP/R3KBNR b KQkq - 0 1")),
            Color.BLACK,
            square(8, 'e'),
            square(8, 'c'),
            square(8, 'a'),
            square(8, 'd')
        )
    }
}