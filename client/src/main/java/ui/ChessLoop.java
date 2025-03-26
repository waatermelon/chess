package ui;

import server.Server;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessLoop {

    boolean debug;
    int debugLoopCounter = 0;
    Server debugServer;

    volatile boolean running;
    ServerFacade facade;

    public ChessLoop(int port, boolean debug) {
        this.debug = debug;
        if(debug) {
            debugServer = new Server();
            debugServer.run(port);
            System.out.println("Running Server and Client on Port: " + port);
        }
        facade = new ServerFacade(Integer.toString(port));
    }

    public void run() {
        running = true;
        System.out.println("♕ CS 240 Chess - Type \"help\" for Commands ♕");
        while (running) {
            if(debug) {
                Thread.onSpinWait();
                debugLoopCounter++;
                printToDebug("DEBUG: iteration " + debugLoopCounter);
            }

            // get input
            String[] args = getInput();
            if (!gaurdClause(args)) {
                System.out.println("Please Enter a Valid Command. Type \"help\" for Commands.");
            }

            // run
            switch (args[0]) {
                case "":
                    break;
            }
        }
    }

    public void exit() {
        running = false;
    }

    public String[] getInput() {
        System.out.print("[" + facade.getUserLoggedIn() + "] >>> ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().strip().toLowerCase();

        if (input.isEmpty()) {
            if (debug) {
                printToDebug("DEBUG: args was empty");
            }
            return new String[0];
        }
        String[] args = input.split(" ");
        if(debug) {
            StringBuilder sb = new StringBuilder();
            sb.append("DEBUG: arg-count = ").append(args.length).append(", args = ");
            sb.append(String.join(", ", args));
            printToDebug(sb.toString());
        }
        return args;
    }

    public boolean gaurdClause(String[] args) {
        if(args.length <= 0) {
            return false;
        }
        return true;
    }

    private void printToDebug(String string) {
        System.out.print(SET_TEXT_COLOR_RED);
        System.out.println(string);
        System.out.print(RESET_TEXT_COLOR);
    }
}
