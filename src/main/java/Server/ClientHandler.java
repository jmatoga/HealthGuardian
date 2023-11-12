package Server;

import Client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

class ClientHandler implements Callable<String> {
    private final Socket clientSocket;
    private final SQLEngine sqlEngine;
    private final int clientId;

    public ClientHandler(Socket clientSocket, SQLEngine sqlEngine, int clientId) {
        this.clientSocket = clientSocket;
        this.sqlEngine = sqlEngine;
        this.clientId = clientId;
    }

    public String call() {
        try (
                // Create input and output streams
                BufferedReader ReadFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter SendToClient = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            SendToClient.println(clientId);
            while (true) {
                String serverMessage = ReadFromClient.readLine();

                if (serverMessage != null) {
                    System.out.println("Server: Received message from client: " + serverMessage);
                }
                if (serverMessage.startsWith("PRINT:")) {
                    // Print the message, excluding the "PRINT:" prefix
                    System.out.println(serverMessage.substring(6));
                } else if (serverMessage.startsWith("SEND:")) {
                    // Send data to the server
                    System.out.println(serverMessage.substring(5));
                    String clientMessage = System.console().readLine();
                    SendToClient.println(clientMessage);
                } else if (serverMessage.startsWith("EXIT:")) {
                    // Exit the loop
                    System.out.println(serverMessage.substring(5));
                    break;
                } else if (serverMessage.startsWith("LOGIN:")) {
                    // TODO move this to separate function
                    String[] loginInfo = serverMessage.substring(6).split(",");
                    String clientId = loginInfo[0];
                    String username = loginInfo[1];
                    String password = loginInfo[2];

                    String[] loginResult = sqlEngine.loginToAccount(Integer.parseInt(clientId),username, password);
                    System.out.println();
                    System.out.println();
                    if(Boolean.parseBoolean(loginResult[0])) {
                        SendToClient.println(""); // This is message to the client reader
                        SendToClient.println("Logged successfully. Your user_id: " + loginResult[1]); // This is message to the LogInController
                    }
                    else {
                        SendToClient.println(""); // This is message to the client reader
                        SendToClient.println("Wrong password."); // This is message to the LogInController
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Client with id: " + Client.clientId + " disconnected.");
        }
        return "Task completed";
    }
}
