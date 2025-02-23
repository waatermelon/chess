# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

# Sequence Diagram:
[diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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)

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
