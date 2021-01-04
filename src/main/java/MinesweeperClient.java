import java.util.Optional;

/**
 * A Minesweeper API client that encapsulates authenticated tokens.
 * The client injects the authenticated user token in all the requests.
 */
public class MinesweeperClient {

    private final HTTPClient HTTPClient;
    private Token token;

    /**
     * Creates a new Minesweeper API client
     *
     * @param hostname the hostname of the Minesweeper API server
     * @param port     the port of the Minesweeper API server
     */
    public MinesweeperClient(String hostname, int port) {
        this.HTTPClient = new HTTPClient(hostname, port);
    }

    /**
     * Performs a user login to the Minesweeper API server
     * On success, all subsequent requests will be performed as authored by that user
     *
     * @param username the username
     * @param password the password
     * @return true if login was successful, false otherwise
     */
    public boolean login(String username, String password) {
        Optional<Token> token = this.HTTPClient.login(username, password);

        if (token.isEmpty())
            return false;

        this.token = token.get();
        return true;
    }

    /**
     * Returns an array of game identifiers of the authenticated user
     * Throws a RuntimeException if the client is not authenticated.
     *
     * @return an array of game identifiers
     */
    public Integer[] list_games() {
        if (token == null)
            throw new RuntimeException("Not authenticated");

        Optional<Integer[]> response = HTTPClient.list_games(token.getToken());

        if (response.isEmpty()) {
            throw new RuntimeException("Request failed");
        }

        return response.get();
    }

    /**
     * Creates a new game for the authenticated user with the given dimensions
     * @param rows the number of rows
     * @param cols the number of columns
     * @param mines the number of mines
     * @return a new game instance
     */
    public Game new_game(int rows, int cols, int mines) {
        if (token == null)
            throw new RuntimeException("Not authenticated");

        Optional<GameState> response = HTTPClient.new_game(token.getToken(), rows, cols, mines);

        if (response.isEmpty()) {
            throw new RuntimeException("Request failed");
        }

        return new Game(this, response.get());
    }


    /**
     * Returns a new game instance corresponding to the provided game id
     * Throws a RuntimeException if the client is not authenticated.
     *
     * @param gameId the game identifier
     * @return a game instance
     */
    public Game get_game(int gameId) {
        if (token == null)
            throw new RuntimeException("Not authenticated");

        Optional<GameState> response = HTTPClient.get_game(token.getToken(), gameId);

        if (response.isEmpty())
            throw new RuntimeException("Request failed");

        return new Game(this, response.get());
    }

    /**
     * Performs an action on a cell of the given game id
     * Throws a RuntimeException if the client is not authenticated.
     *
     * @param gameId the game id
     * @param row    the cell row
     * @param col    the cell column
     * @param status the desired status for the cell, it can be:
     *               - "?" for mark
     *               - "F" for flag
     *               - " " for swipe
     * @return true if the action was executed successfully
     */
    public boolean do_action(int gameId, int row, int col, String status) {
        if (token == null)
            throw new RuntimeException("Not authenticated");

        return this.HTTPClient.do_action(this.token.getToken(), gameId, row, col, status);
    }
}
