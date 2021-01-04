import java.util.Date;

/**
 * A game state DTO
 */
public class GameState {

    public int id;
    public int user_id;
    public Date started_at;
    public Date ended_at;
    public String game_status;
    public String[][] board;

    public GameState() {

    }

    public void setBoard(String[][] board) {
        this.board = board;
    }
}
