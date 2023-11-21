package Server;

import Client.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;
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
                    String[] resources = serverMessage.substring(6).split(",");
                    String clientId = resources[0];
                    String username = resources[1];
                    String password = resources[2];
                    String[] loginResult = sqlEngine.loginToAccount(Integer.parseInt(clientId),username, password);

                    if(Boolean.parseBoolean(loginResult[0])) {
                        SendToClient.println(""); // This is message to the client reader
                        SendToClient.println("Logged successfully. Your user_id: " + loginResult[1]); // This is message to the LogInController
                    } else {
                        SendToClient.println(""); // This is message to the client reader
                        SendToClient.println("Wrong password."); // This is message to the LogInController
                    }
                } else if (serverMessage.startsWith("GET_USER_DATA:")) {
                    String[] resources = serverMessage.substring(14).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] dataResult = sqlEngine.getData(Integer.parseInt(clientId), userId);
                    SendToClient.println(""); // This is message to the client reader
                    SendToClient.println(Arrays.toString(dataResult)); // This is message to the LogInController

                } else if (serverMessage.startsWith("CHECK_IF_USER_EXISTS:")) {
                    String[] resources = serverMessage.substring(21).split(",");
                    String clientId = resources[0];
                    String username = resources[1];

                    boolean existingResult = sqlEngine.checkIfUserExists(Integer.parseInt(clientId), username);
                    SendToClient.println(""); // This is message to the client reader
                    SendToClient.println("EXISTING RESULT:" + existingResult); // This is message to the LogInController

                } else if (serverMessage.startsWith("CHECK_ONE_TIME_CODE:")) {
                    String[] resources = serverMessage.substring(20).split(",");
                    String clientId = resources[0];
                    String oneTimeCode = resources[1];
                    String firstname = resources[2];
                    String lastname = resources[3];
                    String email = resources[4];
                    String phoneNumber = resources[5];
                    String pesel = resources[6];
                    String username = resources[7];
                    String password = resources[8];

                    String codeResult = sqlEngine.checkOneTimeCode(Integer.parseInt(clientId), oneTimeCode, firstname, lastname, email, phoneNumber, pesel, username, password);
                    SendToClient.println(""); // This is message to the client reader
                    SendToClient.println("CODE RESULT:" + codeResult); // This is message to the LogInController
                } else if (serverMessage.startsWith("INSERT_USER_BASIC_DATA:")) {
                    String[] resources = serverMessage.substring(23).split(",");
                    String clientId = resources[0];
                    String birthdayDate = resources[1];
                    String weight = resources[2];
                    String height = resources[3];
                    String temperature = resources[4];
                    String systolic_pressure = resources[5];
                    String diastolic_pressure = resources[6];
                    String entryDate = resources[7];
                    String userId = resources[8];

                    String codeResult = sqlEngine.insertUserBasicData(Integer.parseInt(clientId), birthdayDate, weight, height, temperature, systolic_pressure, diastolic_pressure, entryDate, userId);
                    SendToClient.println(""); // This is message to the client reader
                    SendToClient.println("INSERTING USER BASIC DATA RESULT:" + codeResult); // This is message to the LogInController
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("Client with id: " + Client.clientId + " disconnected."); // TODO repair clientID
        }
        return "Task completed";
    }
}
