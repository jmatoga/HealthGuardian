package Server;

import Client.Client;
import utils.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

                if (serverMessage != null)
                    System.out.println("\nClient " + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + ": " + serverMessage.substring(0, serverMessage.indexOf(":")));
                else
                    System.out.println(Color.ColorString("ERROR! Something went wrong! Server send null message.", Color.ANSI_RED));

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
                        SendToClient.println("Logged successfully. Your user_id: " + loginResult[1]);
                    } else {
                        SendToClient.println("Wrong password.");
                    }
                } else if (serverMessage.startsWith("GET_USER_DATA:")) {
                    String[] resources = serverMessage.substring(14).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] dataResult = sqlEngine.getData(Integer.parseInt(clientId), userId);
                    SendToClient.println(Arrays.toString(dataResult));

                } else if (serverMessage.startsWith("GET_CLINICS:")) {
                    String clientId = serverMessage.substring(12);

                    String[][] dataResult = sqlEngine.getClinics(Integer.parseInt(clientId));
                    SendToClient.println(Arrays.deepToString(dataResult));

                } else if (serverMessage.startsWith("GET_SETTINGS:")) {
                    String[] resources = serverMessage.substring(13).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] settingsResult = sqlEngine.getSettings(Integer.parseInt(clientId), userId);
                    SendToClient.println(Arrays.toString(settingsResult));

                } else if (serverMessage.startsWith("SET_SETTINGS:")) {
                    String[] resources = serverMessage.substring(13).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];
                    String bmiSetting = resources[2];
                    String ageSetting = resources[3];
                    String dateSetting = resources[4];
                    String setting4 = resources[5];
                    String setting5 = resources[6];

                    String settingsChangedResult = sqlEngine.setSettings(Integer.parseInt(clientId), userId, bmiSetting, ageSetting, dateSetting, setting4, setting5);
                    SendToClient.println(settingsChangedResult);

                } else if (serverMessage.startsWith("CHECK_IF_USER_EXISTS:")) {
                    String[] resources = serverMessage.substring(21).split(",");
                    String clientId = resources[0];
                    String username = resources[1];

                    boolean existingResult = sqlEngine.checkIfUserExists(Integer.parseInt(clientId), username);
                    SendToClient.println("EXISTING RESULT:" + existingResult);

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
                    SendToClient.println("CODE RESULT:" + codeResult);

                } else if (serverMessage.startsWith("UPDATE_USER_BASIC_DATA:")) {
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

                    String updateResult = sqlEngine.updateUserBasicData(Integer.parseInt(clientId), birthdayDate, weight, height, temperature, systolic_pressure, diastolic_pressure, entryDate, userId);
                    SendToClient.println(updateResult);
                }
            }
        } catch (IOException e) {
            System.out.println("\nClient with id: " + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + " disconnected.");
        }
        return "Error with ClientHandler!";
    }
}
