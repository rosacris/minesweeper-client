import org.apache.commons.cli.*;

import java.util.Arrays;

public class Application {

    public static void main(String[] args) {

        Options options = new Options();
        options.addRequiredOption("h", "host", true, "Hostname of Minesweeper API server");
        options.addRequiredOption("p", "port", true, "Port of Minesweeper API server");
        options.addRequiredOption("u", "username", true, "User name");
        options.addRequiredOption("pw", "password", true, "Password");

        Option new_game = Option.builder("n").longOpt("new").hasArg().argName("size").desc("Creates a new game of <size> = <row, cols, mines>").build();
        Option get_game = Option.builder("g").longOpt("get").hasArg().argName("id").desc("Gets game <id>").build();
        Option list_games = Option.builder("l").longOpt("list").desc("List user games").build();
        Option mark = Option.builder("m").longOpt("mark").hasArg().argName("cell").desc("Marks a <cell> = <game_id,row,col>").build();
        Option flag = Option.builder("f").longOpt("flag").hasArg().argName("cell").desc("Flags a <cell> = <game_id,row,col>").build();
        Option swipe = Option.builder("s").longOpt("swipe").hasArg().argName("cell").desc("Swipes a <cell> = <game_id,row,col>").build();

        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(true);
        optionGroup.addOption(new_game);
        optionGroup.addOption(get_game);
        optionGroup.addOption(list_games);
        optionGroup.addOption(mark);
        optionGroup.addOption(flag);
        optionGroup.addOption(swipe);

        options.addOptionGroup(optionGroup);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            String hostname = cmd.getOptionValue("h");
            int port = Integer.parseInt(cmd.getOptionValue("p"));
            MinesweeperClient client = new MinesweeperClient(hostname, port);
            boolean loginSuccess = client.login(cmd.getOptionValue("u"), cmd.getOptionValue("pw"));

            if (loginSuccess) {
                if (cmd.hasOption("l")) {
                    // List games
                    System.out.println(Arrays.toString(client.list_games()));
                } else if (cmd.hasOption("n")) {
                    // New games
                    String[] new_args = cmd.getOptionValue("n").split(",");
                    Game game = client.new_game(Integer.parseInt(new_args[0]), Integer.parseInt(new_args[1]), Integer.parseInt(new_args[2]));
                    dumpGame(game);
                } else if (cmd.hasOption("g")) {
                    // Get game
                    int gameId = Integer.parseInt(cmd.getOptionValue("g"));
                    Game game = client.get_game(gameId);
                    dumpGame(game);
                } else if (cmd.hasOption("m")) {
                    // Mark cell of game
                    String[] action_args = cmd.getOptionValue("m").split(",");
                    int gameId = Integer.parseInt(action_args[0]);
                    int row = Integer.parseInt(action_args[1]);
                    int col = Integer.parseInt(action_args[2]);
                    Game game = client.get_game(gameId);
                    game.mark(row, col);
                    dumpGame(game);
                } else if (cmd.hasOption("f")) {
                    // Flag cell of game
                    String[] action_args = cmd.getOptionValue("f").split(",");
                    int gameId = Integer.parseInt(action_args[0]);
                    int row = Integer.parseInt(action_args[1]);
                    int col = Integer.parseInt(action_args[2]);
                    Game game = client.get_game(gameId);
                    game.flag(row, col);
                    dumpGame(game);
                } else if (cmd.hasOption("s")) {
                    // Swipe cell of game
                    String[] action_args = cmd.getOptionValue("s").split(",");
                    int gameId = Integer.parseInt(action_args[0]);
                    int row = Integer.parseInt(action_args[1]);
                    int col = Integer.parseInt(action_args[2]);
                    Game game = client.get_game(gameId);
                    game.swipe(row, col);
                    dumpGame(game);
                }
            } else {
                System.out.println("Invalid username or password");
            }

        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            System.err.println("Parsing failed.  Reason: " + e.getMessage());
            formatter.printHelp("minesweeper-client", options);
        }
    }

    private static void dumpGame(Game game) {
        System.out.println("Game: " + game.getId());
        System.out.println("Play time: " + game.elapsed() + " seconds");
        System.out.println(game.toString());
        System.out.println("Status: " + game.get_status());
        System.out.println("Mines count: " + game.get_mines_count());
    }
}
