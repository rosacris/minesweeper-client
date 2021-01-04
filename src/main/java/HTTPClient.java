import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;

/**
 * An HTTP client for the Minesweeper API
 * This client handles requests and JSON parsing/serialization
 */
public class HTTPClient {

    private final String hostname;
    private final int port;
    private final HttpClient httpClient;

    /**
     * Creates a new instance of the Minesweeper HTTPClient
     *
     * @param hostname the host of the Minesweeper API server
     * @param port     the port of the Minesweeper API server
     */
    public HTTPClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    /**
     * Issues a login request for the given username and password
     *
     * @param username the username
     * @param password the password
     * @return a user token used to identify all future requests from this user
     */
    public Optional<Token> login(String username, String password) {
        try {
            ObjectMapper requestObjectMapper = new ObjectMapper();
            ObjectMapper responseObjectMapper = new ObjectMapper();

            // Build login request body
            Map<String, String> payload = Map.of("username", username, "password", password);
            String requestBody = requestObjectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(payload);

            URI uri = new URI("http", null, hostname, port, "/login", null, null);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return Optional.of(responseObjectMapper.readValue(response.body(), Token.class));
        } catch (InterruptedException | IOException | URISyntaxException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Lists all the game identifiers of the user authenticated with the given token
     *
     * @param token the user token
     * @return a list of all user game identifiers or empty if the request fails.
     */
    public Optional<Integer[]> list_games(String token) {
        try {
            ObjectMapper responseMapper = new ObjectMapper();
            URI uri = new URI("http", null, hostname, port, "/games", null, null);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .headers("authorization", token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(responseMapper.readValue(response.body(), Integer[].class));

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Returns the game state of the given game identifier
     *
     * @param token  the user token
     * @param gameId the game id
     * @return the game state or empty if the request fails
     */
    public Optional<GameState> get_game(String token, int gameId) {
        try {
            ObjectMapper responseMapper = new ObjectMapper();
            URI uri = new URI("http", null, hostname, port, "/games/" + gameId, null, null);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .headers("authorization", token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(responseMapper.readValue(response.body(), GameState.class));

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Creates a new game for the user with the given dimensions
     *
     * @param token the user token
     * @param rows  the number of rows
     * @param cols  the number of columns
     * @param mines the number of mines
     * @return the new game state
     */
    public Optional<GameState> new_game(String token, int rows, int cols, int mines) {
        try {
            ObjectMapper responseMapper = new ObjectMapper();
            String query = "rows=" + rows + "&cols=" + cols + "&mines=" + mines;
            URI uri = new URI("http", null, hostname, port, "/games", query, null);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .headers("authorization", token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(responseMapper.readValue(response.body(), GameState.class));

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Performs an action on a cell of the given user game identifier
     *
     * @param token  the user token
     * @param gameId the game id
     * @param row    the cell row
     * @param col    the cell column
     * @param status the desired status for the cell, it can be:
     *               - "?" for mark
     *               - "F" for flag
     *               - " " for swipe
     * @return true if the action request succeeded, false otherwise
     */
    public boolean do_action(String token, int gameId, int row, int col, String status) {
        try {
            ObjectMapper requestObjectMapper = new ObjectMapper();
            ObjectMapper responseMapper = new ObjectMapper();

            // Build login request body
            Map payload = Map.of("row", row, "col", col, "status", status);
            String requestBody = requestObjectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(payload);

            String path = "/games/" + gameId + "/board";
            URI uri = new URI("http", null, hostname, port, path, null, null);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .header("Content-Type", "application/json")
                    .headers("authorization", token)
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return true;

        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}