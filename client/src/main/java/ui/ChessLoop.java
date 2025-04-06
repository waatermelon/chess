package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.internal.LinkedTreeMap;
import model.GameData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessLoop {
    Scanner scanner;
    volatile boolean running;
    ServerFacade facade;

    ArrayList<GameData> games;
    BoardPrinter boardPrinter;

    public ChessLoop(int port, boolean debug) {
        facade = new ServerFacade(Integer.toString(port));
        this.games = new ArrayList<GameData>();
        this.boardPrinter = new BoardPrinter();
    }

    public void run() {
        running = true;
        scanner = new Scanner(System.in);
        System.out.println("♕ CS 240 Chess - Type \"help\" for Commands ♕");
        while (running) {
            String[] args = getInput();
            if (!gaurdClause(args)) {
                System.out.println("Please Enter a Valid Command. Type \"help\" for Commands.");
            }

            switch (args[0]) {
                case "help":
                    printHelp();
                    break;

                case "quit": case "exit":
                    exit();
                    break;

                case "login":
                    if(args.length < 3) {
                        System.out.println("Illegible Login Command. Type \"help\" for a Guide.");
                        continue;
                    }
                    if (facade.login(args[1], args[2])) {
                        System.out.println("Logged in Successfully! Have fun!");
                    } else {
                        System.out.println("Login Unsuccessful. Please Retry.");
                    }
                    break;

                case "register":
                    if(args.length < 4) {
                        System.out.println("Illegible Register Command. Type \"help\" for a Guide.");
                        continue;
                    }
                    if (facade.register(args[1], args[2], args[3])) {
                        System.out.println("Registered Successfully! Have fun!");
                    } else {
                        System.out.println("Register Unsuccessful. Please Retry.");
                    }
                    break;
                case "logout":
                    if (facade.logout()) {
                        System.out.println("Logged Out Successfully!");
                    } else {
                        System.out.println("You are not Logged In.");
                    }
                    break;
                case "list":
                    if (amLoggedIn()) {
                        ArrayList<LinkedTreeMap> listedGames = facade.listGames();
                        printGames(listedGames);
                        games.clear();
                        for (LinkedTreeMap listedGame : listedGames) {
                            int gameID = (int)(double) listedGame.get("gameID");
                            String whiteUser = (String) listedGame.get("whiteUsername");
                            String blackUser = (String) listedGame.get("blackUsername");
                            String gameName = (String) listedGame.get("gameName");
                            ChessGame game = convertDataToGame(listedGame.get("game"));
                            GameData gameData = new GameData(gameID, blackUser, whiteUser, gameName, game);
                            games.add(gameData);
                        }
                    } else {
                        System.out.println("Log in to use this command.");
                    }
                    break;
                case "create":
                    if (amLoggedIn()) {
                        if(args.length < 2) {
                            System.out.println("Illegible Create Command. Type \"help\" for a Guide.");
                            continue;
                        }
                        double gameID = facade.createGame(args[1]);
                        if (gameID == 0) {
                            System.out.println("Unable to create game. Try again.");
                        } else {
                            System.out.println("Created game with unique ID: " + (int) gameID);

                            ArrayList<LinkedTreeMap> listedGames = facade.listGames();
                            games.clear();
                            for (LinkedTreeMap listedGame : listedGames) {
                                int newGameID = (int)(double) listedGame.get("gameID");
                                String whiteUser = (String) listedGame.get("whiteUsername");
                                String blackUser = (String) listedGame.get("blackUsername");
                                String gameName = (String) listedGame.get("gameName");
                                ChessGame game = convertDataToGame(listedGame.get("game"));
                                GameData gameData = new GameData(newGameID, blackUser, whiteUser, gameName, game);
                                games.add(gameData);
                            }
                        }
                    } else {
                        System.out.println("Log in to use this command.");
                    }
                    break;
                case "join":
                    runJoin(args);
                    break;
                case "view":
                    runView(args);
                    break;
                default:
                    System.out.println("Command not found. Type \"help\" for a list of Valid Commands.");
                    break;
            }
        }
    }

    private void  runView(String[] args) {
        if (amLoggedIn()) {
            if(args.length < 2 || !args[1].matches("^-?\\d+$")) {
                System.out.println("Illegible Join Command. Type \"help\" for a Guide.");
                return;
            }
            double gameNumber = Double.parseDouble(args[1]);
            if (facade.viewGame(gameNumber)) {
                System.out.println("Successfully viewing game!");
                GameData game = games.get(((int) gameNumber) - 1);
                boardPrinter.printBoard(game, ChessGame.TeamColor.WHITE);
            } else {
                System.out.println("Unable to view game. Try again later.");
            }
        } else {
            System.out.println("Log in to use this command.");
        }
    }

    private void runJoin(String[] args) {
        if (amLoggedIn()) {
            if(args.length < 3 ||
                    !args[1].matches("(?i)BLACK|WHITE") ||
                    !args[2].matches("^-?\\d+$")) {
                System.out.println("Illegible Join Command. Type \"help\" for a Guide.");
                return;
            }
            String teamColor = args[1].toUpperCase();
            double gameNum = Double.parseDouble(args[2]);

            if (facade.joinGame(teamColor, gameNum)) {
                System.out.println("Successfully joined game!");

                boardPrinter.printBoard(games.get((int) gameNum - 1),
                        (teamColor.equals("WHITE")) ?
                                ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
            } else {
                System.out.println("Unable to join game. Try again later.");
            }
        } else {
            System.out.println("Log in to use this command.");
        }
    }

    public void exit() {
        running = false;
        scanner.close();
        System.out.println("♕ Thanks for playing CS 240 Chess ♕");
    }

    public String[] getInput() {
        System.out.print("[" + facade.getUserLoggedIn() + "] >>> ");
        String input = scanner.nextLine().strip().toLowerCase();

        if (input.isEmpty()) {
            return new String[0];
        }
        String[] args = input.split(" ");
        return args;
    }

    public boolean gaurdClause(String[] args) {
        if(args.length <= 0) {
            return false;
        }
        return true;
    }

    private void printHelp() {
        System.out.print(SET_TEXT_COLOR_BLUE);
        StringBuilder sb = new StringBuilder();
        sb.append("\thelp - shows all commands\n");
        sb.append("\tquit / exit - exits game\n");
        sb.append("\tlogin <USERNAME> <PASSWORD> - logs into account\n");
        sb.append("\tregister <USERNAME> <PASSWORD> <EMAIL> - creates new account\n");
        sb.append("\tlogout - logs player out\n");
        sb.append("\tcreate <GAME NAME> - creates a new chess game\n");
        sb.append("\tlist - lists all available games\n");
        sb.append("\tjoin <PLAYERCOLOR> <GAMENUMBER> - joins game as player\n");
        sb.append("\tview <GAMENUMBER> - joins game as spectator\n");

        System.out.println(sb.toString());
        System.out.print(RESET_TEXT_COLOR);
        //add postlogin later

    }

    private boolean amLoggedIn() {
        return Objects.equals(facade.getUserLoggedIn(), "LOGGED_IN");
    }

    private void printGames(ArrayList<LinkedTreeMap> games) {
        StringBuilder sb = new StringBuilder();
        sb.append("Games:\n");
        int gameIterator = 0;
        for (LinkedTreeMap game : games) {

            gameIterator++;
            int gameID = (int) Math.floor((double) game.get("gameID"));
            String gameName = String.valueOf(game.get("gameName"));
            sb.append("Game #").append(gameIterator)
                    .append(", Game Name: ").append(gameName).append("\n");
            String whiteUser = String.valueOf(game.get("whiteUsername"));
            String blackUser = String.valueOf(game.get("blackUsername"));
            sb.append("\tWhite User: ").append((whiteUser == "null") ? "" : whiteUser);
            sb.append(", Black User: ").append((blackUser == "null") ? "" : blackUser);
            sb.append("\n");
            convertDataToGame(game.get("game"));
        }
        System.out.print(sb.toString());
    }

    private ChessGame convertDataToGame(Object treeMap) {
        LinkedTreeMap currentData = (LinkedTreeMap) treeMap;
        LinkedTreeMap mapBoard = (LinkedTreeMap) currentData.get("board");
        ArrayList<ArrayList<LinkedTreeMap>> arrayListBoard =
                (ArrayList<ArrayList<LinkedTreeMap>>) mapBoard.get("board");
        ChessGame game = new ChessGame();
        ChessBoard board = new ChessBoard();
        for (int i = 1; i <= 8; ++i) {
            for (int j = 1; j <= 8; ++j) {
                LinkedTreeMap map = arrayListBoard.get(i - 1).get(j - 1);

                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = convertDataToPiece(map);
                board.addPiece(position, piece);
            }
        }
        game.setBoard(board);
        return game;
    }

    private ChessPiece convertDataToPiece(LinkedTreeMap treeMap) {
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.BLACK;
        if (treeMap == null) {
            return null;
        }
        if (String.valueOf(treeMap.get("pieceColor")).equals("WHITE")) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        ChessPiece.PieceType type = ChessPiece.PieceType.PAWN;
        switch(String.valueOf(treeMap.get("type"))) {
            case "ROOK":
                type = ChessPiece.PieceType.ROOK;
                break;
            case "KNIGHT":
                type = ChessPiece.PieceType.KNIGHT;
                break;
            case "BISHOP":
                type = ChessPiece.PieceType.BISHOP;
                break;
            case "QUEEN":
                type = ChessPiece.PieceType.QUEEN;
                break;
            case "KING":
                type = ChessPiece.PieceType.KING;
                break;
            case "PAWN":
                type = ChessPiece.PieceType.PAWN;
                break;
        }
        return new ChessPiece(teamColor, type);
    }
}
