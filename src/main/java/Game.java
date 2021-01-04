import java.time.Duration;
import java.time.Instant;
import java.util.StringJoiner;

/**
 * Represents a single Minesweeper game
 */
public class Game {
    private final MinesweeperClient minesweeperClient;
    private GameState gameState;

    /**
     * Creates a new game with the given state and API client
     *
     * @param minesweeperClient the Minesweeper API client to use
     * @param gameState         the current state of the game
     */
    public Game(MinesweeperClient minesweeperClient, GameState gameState) {
        this.minesweeperClient = minesweeperClient;
        this.gameState = gameState;
    }

    /**
     * Returns a string of the board state.
     * <p>
     * Each cell can contain any of the following values:
     * - "#": unexplored
     * - "?": marked by the user
     * - "F": flagged by the user
     * - "*": mine was swiped (boom!)
     * - " ": cell was cleared
     * - "[number]": cell was cleared and has [number] amount of mines around
     *
     * @return a string representation of the board
     */
    public String toString() {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (String[] row : gameState.board) {
            sj.add("| " + String.join(" | ", row) + " |");
        }
        return sj.toString();
    }

    /**
     * Returns the amount of time elapsed since the creation of the game
     *
     * @return the elapsed time in seconds
     */
    public long elapsed() {
        Instant from = gameState.started_at.toInstant();
        Instant to = gameState.ended_at != null ? gameState.ended_at.toInstant() : Instant.now();
        Duration d = Duration.between(from, to);
        return d.toSeconds();
    }

    /**
     * Returns the status of the game
     *
     * @return possible values are "undecided", "won", "lost"
     */
    public String get_status() {
        return gameState.game_status;
    }

    /**
     * Returns the amount of mines in the game
     * @return the amount of mines
     */
    public int get_mines_count() {
        return gameState.mines;
    }

    /**
     * Marks a cell and updates the game state if the game is undecided
     *
     * @param row the cell row
     * @param col the cell column
     * @return true if the mark succeeded, false otherwise
     */
    public boolean mark(int row, int col) {
        // Noop if the game is over
        if (!get_status().equals("undecided"))
            return false;

        boolean result = minesweeperClient.do_action(gameState.id, row, col, "?");
        if(result) {
            gameState = minesweeperClient.get_game(gameState.id).gameState;
        }
        return result;
    }

    /**
     * Flags a cell and updates the game state if the game is undecided
     *
     * @param row the cell row
     * @param col the cell column
     * @return true if the flag succeeded, false otherwise
     */
    public boolean flag(int row, int col) {
        // Noop if the game is over
        if (!get_status().equals("undecided"))
            return false;

        boolean result = minesweeperClient.do_action(gameState.id, row, col, "F");
        if(result) {
            gameState = minesweeperClient.get_game(gameState.id).gameState;
        }
        return result;
    }

    /**
     * Swipes a cell and updates the game state if the game is undecided
     *
     * @param row the cell row
     * @param col the cell column
     * @return true if swipe succeeded, false otherwise
     */
    public boolean swipe(int row, int col) {
        // Noop if the game is over
        if (!get_status().equals("undecided"))
            return false;

        boolean result = minesweeperClient.do_action(gameState.id, row, col, " ");
        if(result) {
            gameState = minesweeperClient.get_game(gameState.id).gameState;
        }
        return result;
    }
}
