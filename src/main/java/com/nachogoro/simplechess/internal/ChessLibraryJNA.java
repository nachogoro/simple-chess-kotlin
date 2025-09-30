package com.nachogoro.simplechess.internal;

import com.sun.jna.*;
import com.sun.jna.Structure.FieldOrder;

/**
 * JNA interface for the simple-chess-games C library.
 * <p>
 * This class provides Java bindings for the library's C interface,
 * allowing direct access to all chess game functionality including:
 * - Game creation and management
 * - Move validation and execution
 * - Board state representation
 * - Game history tracking
 */
public class ChessLibraryJNA {

    // ========== ENUMS ==========

    /**
     * The color of each side in a chess game.
     */
    public static class Color {
        public static final int WHITE = 0;
        public static final int BLACK = 1;
    }

    /**
     * The type of a chess piece.
     */
    public static class PieceType {
        public static final int PAWN = 0;
        public static final int ROOK = 1;
        public static final int KNIGHT = 2;
        public static final int BISHOP = 3;
        public static final int QUEEN = 4;
        public static final int KING = 5;
    }

    /**
     * The different types of check which can be caused by a move.
     */
    public static class CheckType {
        public static final int NONE = 0;
        public static final int CHECK = 1;
        public static final int CHECKMATE = 2;
    }

    /**
     * Castling rights bitfield values.
     */
    public static class CastlingRight {
        public static final int WHITE_KING_SIDE = 0x01;
        public static final int WHITE_QUEEN_SIDE = 0x02;
        public static final int BLACK_KING_SIDE = 0x04;
        public static final int BLACK_QUEEN_SIDE = 0x08;
    }

    /**
     * The overall state of a chess game.
     */
    public static class GameState {
        public static final int PLAYING = 0;
        public static final int DRAWN = 1;
        public static final int WHITE_WON = 2;
        public static final int BLACK_WON = 3;
    }

    /**
     * Reasons why a game might be drawn.
     */
    public static class DrawReason {
        public static final int STALEMATE = 0;
        public static final int INSUFFICIENT_MATERIAL = 1;
        public static final int OFFERED_AND_ACCEPTED = 2;
        public static final int THREE_FOLD_REPETITION = 3;
        public static final int FIVE_FOLD_REPETITION = 4;
        public static final int FIFTY_MOVE_RULE = 5;
        public static final int SEVENTY_FIVE_MOVE_RULE = 6;
    }

    // ========== STRUCTS ==========

    /**
     * Represents a square on the chess board.
     * Squares are identified by rank (1-8) and file ('a'-'h').
     */
    @FieldOrder({"rank", "file"})
    public static class Square extends Structure {
        /** Rank (1-8) */
        public byte rank;

        /** File ('a'-'h') */
        public byte file;

        /**
         * Default constructor for JNA.
         */
        public Square() {}

        /**
         * Constructor with rank and file.
         * @param rank the rank (1-8)
         * @param file the file ('a'-'h')
         */
        public Square(int rank, char file) {
            this.rank = (byte) rank;
            this.file = (byte) file;
        }

        public static class ByValue extends Square implements Structure.ByValue {
            /**
             * Default constructor for JNA.
             */
            public ByValue() {}
            /**
             * Constructor with rank and file.
             * @param rank the rank (1-8)
             * @param file the file ('a'-'h')
             */
            public ByValue(int rank, char file) { super(rank, file); }
        }
    }

    /**
     * Represents a chess piece with its type and color.
     */
    @FieldOrder({"type", "color"})
    public static class Piece extends Structure {
        /** Type of the piece */
        public int type;

        /** Color of the piece */
        public int color;

        /**
         * Default constructor for JNA.
         */
        public Piece() {}

        /**
         * Constructor with type and color.
         * @param type the piece type
         * @param color the piece color
         */
        public Piece(int type, int color) {
            this.type = type;
            this.color = color;
        }


        public static class ByValue extends Piece implements Structure.ByValue {
            /**
             * Default constructor for JNA.
             */
            public ByValue() {}
            /**
             * Constructor with type and color.
             * @param type the piece type
             * @param color the piece color
             */
            public ByValue(int type, int color) { super(type, color); }
        }
    }

    /**
     * Describes a move which can be made by a player.
     */
    @FieldOrder({"piece", "src", "dst", "is_promotion", "promoted_to"})
    public static class PieceMove extends Structure {
        /** The piece whose movement is described */
        public Piece.ByValue piece;

        /** The original square of the moved piece */
        public Square.ByValue src;

        /** The final square of the moved piece */
        public Square.ByValue dst;

        /** Indicates if the move represents a pawn promotion */
        public byte is_promotion;

        /** Returns the new type of the promoted pawn (only valid if is_promotion is true) */
        public int promoted_to;

        /**
         * Default constructor for JNA.
         */
        public PieceMove() {
            piece = new Piece.ByValue();
            src = new Square.ByValue();
            dst = new Square.ByValue();
        }

        public static class ByValue extends PieceMove implements Structure.ByValue {
            /**
             * Default constructor for JNA.
             */
            public ByValue() {}
        }
    }

    /**
     * Describes a move that has been played in a game of chess.
     */
    @FieldOrder({"move", "is_capture", "captured_piece", "check_type", "offers_draw", "in_algebraic_notation"})
    public static class PlayedMove extends Structure {
        /** The description of the move of the piece */
        public PieceMove.ByValue move;

        /** Indicates if the move captured an opposing piece */
        public byte is_capture;

        /** The opposing piece that was captured (only if is_capture is true) */
        public Piece.ByValue captured_piece;

        /** The type of check delivered by the move */
        public int check_type;

        /** Whether the player offers a draw with this move */
        public byte offers_draw;

        /** The string representation of the move in algebraic notation */
        public byte[] in_algebraic_notation = new byte[8];

        /**
         * Default constructor for JNA.
         */
        public PlayedMove() {
            move = new PieceMove.ByValue();
            captured_piece = new Piece.ByValue();
        }


        public static class ByValue extends PlayedMove implements Structure.ByValue {
            /**
             * Default constructor for JNA.
             */
            public ByValue() {}
        }
    }

    /**
     * Represents the chess board state.
     */
    @FieldOrder({"occupied", "piece_at"})
    public static class Board extends Structure {
        /** Whether the i-th square is occupied by a piece or not */
        public byte[] occupied = new byte[64];

        /** The piece located at the i-th square (only if occupied[i] is true) */
        public Piece.ByValue[] piece_at = new Piece.ByValue[64];

        /**
         * Default constructor for JNA.
         */
        public Board() {
            for (int i = 0; i < 64; i++) {
                piece_at[i] = new Piece.ByValue();
            }
        }

        public static class ByValue extends Board implements Structure.ByValue {
            /**
             * Default constructor for JNA.
             */
            public ByValue() {}
        }
    }

    /**
     * Represents a complete game position.
     */
    @FieldOrder({"board", "active_color", "castling_rights", "half_moves_since_last_capture_or_pawn_advance", "full_moves", "has_en_passant_target", "en_passant_target", "check_status", "fen"})
    public static class GameStage extends Structure {
        /** Current board position */
        public Board.ByValue board;

        /** Whose turn it is */
        public int active_color;

        /** Bitfield of available castling rights */
        public byte castling_rights;

        /** Counter for the 50-move rule */
        public short half_moves_since_last_capture_or_pawn_advance;

        /** Full move number (incremented after Black's move) */
        public short full_moves;

        /** Whether en passant capture is possible */
        public byte has_en_passant_target;

        /** En passant target square (if has_en_passant_target is true) */
        public Square.ByValue en_passant_target;

        /** Current check status */
        public int check_status;

        /** FEN representation of this position */
        public byte[] fen = new byte[90];

        /**
         * Default constructor for JNA.
         */
        public GameStage() {
            board = new Board.ByValue();
            en_passant_target = new Square.ByValue();
        }


        public static class ByValue extends GameStage implements Structure.ByValue {
            /**
             * Default constructor for JNA.
             */
            public ByValue() {}
        }
    }

    /**
     * Represents one entry in the game history.
     */
    @FieldOrder({"fen", "played_move"})
    public static class GameHistoryEntry extends Structure {
        /** FEN representation before this move */
        public byte[] fen = new byte[90];

        /** The move that was played */
        public PlayedMove.ByValue played_move;

        /**
         * Default constructor for JNA.
         */
        public GameHistoryEntry() {
            played_move = new PlayedMove.ByValue();
        }
    }

    /**
     * Represents a complete chess game with all state information.
     */
    @FieldOrder({"state", "draw_reason", "history", "history_size", "available_moves", "available_move_count", "current_stage", "is_draw_claimable", "reason_to_claim_draw"})
    public static class Game extends Structure {
        /** Current game state */
        public int state;

        /** Reason for draw (if state is GameStateDrawn) */
        public int draw_reason;

        /** Array of all moves played in the game */
        public Pointer history;

        /** Number of moves in history */
        public short history_size;

        /** Array of all legal moves in current position */
        public Pointer available_moves;

        /** Number of available moves */
        public short available_move_count;

        /** Current position and game state */
        public GameStage.ByValue current_stage;

        /** Whether a draw can be claimed by the current player */
        public byte is_draw_claimable;

        /** Reason a draw can be claimed (if is_draw_claimable is true) */
        public int reason_to_claim_draw;

        /**
         * Default constructor for JNA.
         */
        public Game() {
            current_stage = new GameStage.ByValue();
        }
    }

    // ========== LIBRARY INTERFACE ==========

    /**
     * Interface to the native chess library functions.
     */
    public interface ChessLib extends Library {
        ChessLib INSTANCE = createInstance();

        /**
         * Factory function to create a new game from the standard starting position.
		 * @return pointer to the new game object
         */
        Pointer simple_chess_create_new_game();

        /**
         * Factory function to create a new game from a given board position.
         * @param fen the FEN string representing the board position
         * @return pointer to the new game object
         */
        Pointer simple_chess_create_game_from_fen(String fen);

        /**
         * Make a move for the player whose turn it is to play.
         * @param game pointer to the game object
         * @param move the move to make
         * @return pointer to the new game state
         */
        Pointer simple_chess_make_move(Pointer game, PieceMove.ByValue move);

        /**
         * Make a move and optionally offer a draw.
         * @param game pointer to the game object
         * @param move the move to make
         * @param offer_draw whether to offer a draw with this move
         * @return pointer to the new game state
         */
        Pointer simple_chess_make_move_with_draw_offer(Pointer game, PieceMove.ByValue move, boolean offer_draw);

        /**
         * Claim a draw if one is available.
         * @param game pointer to the game object
         * @return pointer to the new game state
         */
        Pointer simple_chess_claim_draw(Pointer game);

        /**
         * Resign the game for the specified player.
         * @param game pointer to the game object
         * @param resigner the color of the player who is resigning
         * @return pointer to the new game state
         */
        Pointer simple_chess_resign(Pointer game, int resigner);

        /**
         * Free all memory associated with a game object.
         * @param game pointer to the game object to destroy
         */
        void destroy_game(Pointer game);
    }

    // ========== HELPER METHODS ==========

    /**
     * Helper method to get Game structure from pointer.
     * @param ptr the native pointer to the game structure
     * @return the Game object or null if ptr is null
     */
    public static Game getGameFromPointer(Pointer ptr) {
        if (ptr == null) return null;
        Game game = Structure.newInstance(Game.class, ptr);
        game.read();  // Explicitly read the structure from memory
        return game;
    }

    /**
     * Helper method to get array of PieceMove from pointer and count.
     * @param ptr the native pointer to the moves array
     * @param count the number of moves in the array
     * @return array of PieceMove objects
     */
    public static PieceMove[] getAvailableMovesFromPointer(Pointer ptr, int count) {
        if (ptr == null || count == 0) return new PieceMove[0];

        PieceMove[] moves = new PieceMove[count];
        int moveSize = new PieceMove().size();
        for (int i = 0; i < count; i++) {
            moves[i] = Structure.newInstance(PieceMove.class, ptr.share((long) i * moveSize));
            moves[i].read();  // Explicitly read each structure
        }
        return moves;
    }

    /**
     * Helper method to get array of GameHistoryEntry from pointer and count.
     * @param ptr the native pointer to the history array
     * @param count the number of entries in the array
     * @return array of GameHistoryEntry objects
     */
    public static GameHistoryEntry[] getHistoryFromPointer(Pointer ptr, int count) {
        if (ptr == null || count == 0) return new GameHistoryEntry[0];

        GameHistoryEntry[] history = new GameHistoryEntry[count];
        int entrySize = new GameHistoryEntry().size();
        for (int i = 0; i < count; i++) {
            history[i] = Structure.newInstance(GameHistoryEntry.class, ptr.share((long) i * entrySize));
            history[i].read();  // Explicitly read each structure
        }
        return history;
    }

    /**
     * Helper method to convert byte array to Java string (null-terminated).
     * @param bytes the null-terminated byte array
     * @return the converted string
     */
    public static String byteArrayToString(byte[] bytes) {
        int len = 0;
        while (len < bytes.length && bytes[len] != 0) len++;
        return new String(bytes, 0, len);
    }

    // ========== AUTOMATIC LIBRARY LOADING ==========

    /**
     * Creates the ChessLib instance with automatic platform detection and library loading.
     */
    private static ChessLib createInstance() {
        try {
            // Set up library path with automatic platform detection
            setupNativeLibraryPath();

            // Try to load the C version first (most reliable)
            return Native.load("simple-chess-games-c", ChessLib.class);
        } catch (UnsatisfiedLinkError e) {
            try {
                // Fallback to main library
                return Native.load("simple-chess-games", ChessLib.class);
            } catch (UnsatisfiedLinkError e2) {
                throw new RuntimeException("Failed to load native chess library. " +
                    "Platform: " + System.getProperty("os.name") + " " + System.getProperty("os.arch"), e2);
            }
        }
    }

    /**
     * Sets up the JNA library path by extracting native libraries from JAR resources.
     */
    private static void setupNativeLibraryPath() {
        try {
            // Detect platform
            String platform = detectPlatform();
            String architecture = detectArchitecture();

            // Extract libraries to temp directory
            String tempPath = extractNativeLibraries(platform, architecture);

            // Set JNA library path
            System.setProperty("jna.library.path", tempPath);

        } catch (Exception e) {
            // Fallback: try to use system library path
            System.err.println("Warning: Could not extract native libraries, falling back to system path: " + e.getMessage());
        }
    }

    /**
     * Detects the current platform.
     */
    private static String detectPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("linux")) {
            // Check if we're on Android
            String javaVendor = System.getProperty("java.vendor", "").toLowerCase();
            String javaVmName = System.getProperty("java.vm.name", "").toLowerCase();
            if (javaVendor.contains("android") || javaVmName.contains("dalvik") ||
                System.getProperty("java.specification.vendor", "").toLowerCase().contains("android")) {
                return "android";
            }
            return "linux";
        } else if (osName.contains("windows")) {
            return "windows";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            return "macos";
        } else {
            throw new UnsupportedOperationException("Unsupported platform: " + osName);
        }
    }

    /**
     * Detects the current architecture.
     */
    private static String detectArchitecture() {
        String osArch = System.getProperty("os.arch").toLowerCase();
        String platform = detectPlatform();

        if ("android".equals(platform)) {
            // Android has specific ABI detection
            String abi = System.getProperty("ro.product.cpu.abi");
            if (abi != null) {
                if (abi.startsWith("arm64") || abi.startsWith("aarch64")) return "arm64-v8a";
                if (abi.startsWith("armeabi-v7a") || abi.startsWith("arm")) return "armeabi-v7a";
                if (abi.equals("x86_64")) return "x86_64";
                if (abi.equals("x86")) return "x86";
            }

            // Fallback to os.arch for Android
            if (osArch.contains("aarch64") || osArch.contains("arm64")) return "arm64-v8a";
            if (osArch.contains("arm")) return "armeabi-v7a";
            if (osArch.contains("x86_64") || osArch.contains("amd64")) return "x86_64";
            if (osArch.contains("x86")) return "x86";

            // Default to most common Android architecture
            return "arm64-v8a";
        } else {
            // Desktop platforms
            if (osArch.contains("amd64") || osArch.contains("x86_64")) return "x86_64";
            if (osArch.contains("x86")) return "x86";
            if (osArch.contains("aarch64") || osArch.contains("arm64")) return "arm64";
            if (osArch.contains("arm")) return "arm";

            throw new UnsupportedOperationException("Unsupported architecture: " + osArch);
        }
    }

    /**
     * Extracts native libraries from JAR resources to a temporary directory.
     */
    private static String extractNativeLibraries(String platform, String architecture) throws Exception {
        // Create temp directory
        java.io.File tempDir = java.io.File.createTempFile("chess-native-", "");
        tempDir.delete();
        tempDir.mkdirs();
        tempDir.deleteOnExit();

        // Library names to try
        String[] libraryNames = getLibraryNames(platform);
        String resourcePath = "/native/" + platform + "/" + architecture + "/";

        boolean extracted = false;
        for (String libName : libraryNames) {
            if (extractLibrary(resourcePath + libName, new java.io.File(tempDir, libName))) {
                extracted = true;
            }
        }

        if (!extracted) {
            throw new RuntimeException("No native libraries found for platform: " + platform + "/" + architecture);
        }

        return tempDir.getAbsolutePath();
    }

    /**
     * Gets the library file names for a platform.
     */
    private static String[] getLibraryNames(String platform) {
        switch (platform) {
            case "linux":
            case "android":
                return new String[]{"libsimple-chess-games-c.so", "libsimple-chess-games.so"};
            case "windows":
                return new String[]{"libsimple-chess-games-c.dll", "libsimple-chess-games.dll"};
            case "macos":
                return new String[]{"libsimple-chess-games-c.dylib", "libsimple-chess-games.dylib"};
            default:
                throw new UnsupportedOperationException("Unsupported platform: " + platform);
        }
    }

    /**
     * Extracts a single library file from JAR resources.
     */
    private static boolean extractLibrary(String resourcePath, java.io.File outputFile) {
        try (java.io.InputStream input = ChessLibraryJNA.class.getResourceAsStream(resourcePath)) {
            if (input == null) {
                return false; // Resource not found
            }

            outputFile.getParentFile().mkdirs();
            outputFile.deleteOnExit();

            try (java.io.FileOutputStream output = new java.io.FileOutputStream(outputFile)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }
            }

            // Make executable on Unix systems
            if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
                outputFile.setExecutable(true);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
