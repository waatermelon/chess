package ui;

import chess.*;
import com.google.gson.internal.LinkedTreeMap;
import model.GameData;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessLoop {
    Scanner scanner;
    volatile boolean running;
    ServerFacade facade;
    public GameData currentGame;
    public ChessGame.TeamColor currentColor;
    String username;

    ArrayList<GameData> games;
    BoardPrinter boardPrinter;

    public ChessLoop(int port, boolean debug) {
        facade = new ServerFacade(Integer.toString(port), this);
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
                continue;
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
                        username = args[1];
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
                        username = args[1];
                    } else {
                        System.out.println("Register Unsuccessful. Please Retry.");
                    }
                    break;
                case "logout":
                    if (facade.logout()) {
                        System.out.println("Logged Out Successfully!");
                    } else {
                        System.out.println("Log in to use this command.");
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
                            System.out.println("Created game successfully!");

                            ArrayList<LinkedTreeMap> listedGames = facade.listGames();
                            games.clear();
                            for (LinkedTreeMap listedGame : listedGames) {
                                int newGameID = (int)(double) listedGame.get("gameID");
                                String whiteUser = (String) listedGame.get("whiteUsername");
                                String blackUser = (String) listedGame.get("blackUsername");
                                String gameName = (String) listedGame.get("gameName");
                                ChessGame game = convertDataToGame(listedGame.get("game"));
                                GameData gameData = new GameData(newGameID, blackUser, whiteUser,
                                        gameName, game);
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

    private void runGame(boolean viewing) {
        facade.webSocketConnection();
        int gameID = (int) currentGame.gameID();
        if (viewing) {
            facade.sendMessage(UserGameCommand.CommandType.CONNECT,
                    gameID, username, null, null);
        } else {
            facade.sendMessage(
                    UserGameCommand.CommandType.CONNECT, gameID, username, currentColor, null
            );
        }

        boolean gameRunning = true;

        while (gameRunning) {
            String[] args = getInput();
            if (!gaurdClause(args)) {
                System.out.println("Please Enter a Valid Command. Type \"help\" for Commands.");
            }

            switch(args[0]) {
                case "help":
                    printWSHelp(viewing);
                    break;
                case "redraw":
                    boardPrinter.printBoard(currentGame, currentColor);
                    break;
                case "leave":
                    runLeave(args);
                    gameRunning = false;
                    break;
                case "move":
                    if (viewing) {
                        break;
                    }
                    runMove(args);
                    break;
                case "resign":
                    if (viewing) {
                        break;
                    }
                    runResign(args);
                    break;
                case "highlight":
                    runHighlight(args);
                    break;
                default:
                    System.out.println("Please Enter a Valid Command. Type \"help\" for Commands.");
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
            if (games.size() > (int) gameNumber - 1 && facade.viewGame(gameNumber)) {
                System.out.println("Successfully viewing game!");

                GameData game = games.get(((int) gameNumber) - 1);
                currentGame = games.get((int) gameNumber - 1);
                currentColor = ChessGame.TeamColor.WHITE;
                runGame(true);
            } else {
                currentColor = ChessGame.TeamColor.WHITE;
                currentGame = null;
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

            if (games.size() > (int) gameNum - 1 && facade.joinGame(teamColor, gameNum)) {
                System.out.println("Successfully joined game!");

                currentGame = games.get((int) gameNum - 1);
                currentColor = (teamColor.equals("WHITE") ?
                        ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);

                runGame(false);
            } else {
                currentGame = null;
                currentColor = ChessGame.TeamColor.WHITE;
                System.out.println("Unable to join game. Try again later.");
            }
        } else {
            System.out.println("Log in to use this command.");
        }
    }

    private void runLeave(String[] args) {
        facade.sendMessage(
                UserGameCommand.CommandType.LEAVE, currentGame.gameID(), username, null, null
        );
        facade.closeConnection();
        System.out.println("You have left the game.");
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

    private void printWSHelp(boolean viewing) {
        System.out.print(SET_TEXT_COLOR_BLUE);
        StringBuilder sb = new StringBuilder();
        sb.append("\thelp - shows all available commands\n");

        if (!viewing) {
            sb.append("\tmove <FROM POS> <TO POS> <PROMOTE TO> "+
                    "- Moves a piece on the board (example pos: a1)\n");
            sb.append("\tresign - resigns the game\n");
        }

        sb.append("\tredraw - redraws the game\n");
        sb.append("\tleave - exits the current game\n");
        sb.append("\thighlight <PIECE POS> - highlights available moves for piece\n");

        System.out.println(sb.toString());
        System.out.print(RESET_TEXT_COLOR);
    }

    private void printHelp() {
        System.out.print(SET_TEXT_COLOR_BLUE);
        StringBuilder sb = new StringBuilder();
        sb.append("\thelp - shows all commands\n");

        if (amLoggedIn()) {
            sb.append("\tlogout - logs player out\n");
            sb.append("\tcreate <GAME NAME> - creates a new chess game\n");
            sb.append("\tjoin <PLAYERCOLOR> <GAMENUMBER> - joins game as player\n");
            sb.append("\tview <GAMENUMBER> - joins game as spectator\n");
            sb.append("\tlist - lists all available games\n");
        }

        sb.append("\tquit / exit - exits game\n");
        sb.append("\tlogin <USERNAME> <PASSWORD> - logs into account\n");
        sb.append("\tregister <USERNAME> <PASSWORD> <EMAIL> - creates new account\n");

        System.out.println(sb.toString());
        System.out.print(RESET_TEXT_COLOR);
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

    private ChessPosition getPosition(String pos) {
        if (pos.length() != 2) {
            System.out.println("Invalid position format: " + pos);
            return null;
        }
        char fileChar = pos.charAt(0);
        char rankChar = pos.charAt(1);

        if (fileChar < 'a' || fileChar > 'h') {
            System.out.println("Invalid file (column) in position: " + pos);
            return null;
        }

        if (rankChar < '1' || rankChar > '8') {
            System.out.println("Invalid rank (row) in position: " + pos);
            return null;
        }
        int col = fileChar - 'a' + 1;
        int row = rankChar - '1' + 1;

        return new ChessPosition(row, col);
    }

    private void runMove(String[] args) {
        if (args.length < 3) {
            System.out.println("Invalid move command. Usage: move <FROM POS> <TO POS> <PROMOTE TO>");
            return;
        }

        ChessPosition startPos = getPosition(args[1]);
        ChessPosition endPos = getPosition(args[2]);
        if (startPos == null || endPos == null) {
            System.out.println("Invalid Move: Could not parse board positions.");
            return;
        }

        ChessPiece.PieceType promoteTo = null;
        if (args.length >= 4) {
            promoteTo = convertStringToPiece(args[3].toUpperCase());
        }

        ChessMove move = new ChessMove(startPos, endPos, promoteTo);

        facade.sendMessage(
                UserGameCommand.CommandType.MAKE_MOVE,
                currentGame.gameID(),
                username,
                currentColor,
                move
        );
    }

    private void runResign(String[] args) {
        facade.sendMessage(
                UserGameCommand.CommandType.RESIGN, currentGame.gameID(),
                username, currentColor, null
        );
    }

    private void runHighlight(String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid Highlight: Please try again.");
        }
        ChessPosition pos = getPosition(args[1]);
        if (pos == null) {
            System.out.println("Invalid Highlight Position: Could not parse board positions.");
            return;
        }

        boardPrinter.printHighlightedBoard(currentGame, currentColor, pos);
    }

    private ChessPiece convertDataToPiece(LinkedTreeMap treeMap) {
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.BLACK;
        if (treeMap == null) {
            return null;
        }
        if (String.valueOf(treeMap.get("pieceColor")).equals("WHITE")) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        ChessPiece.PieceType type = convertStringToPiece(String.valueOf(treeMap.get("type")));
        return new ChessPiece(teamColor, type);
    }

    private ChessPiece.PieceType convertStringToPiece(String piece) {
        ChessPiece.PieceType type;
        switch(piece) {
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
            default:
                type = null;
                break;
        }
        return  type;
    }
}
