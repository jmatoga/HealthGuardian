package Server;

import utils.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * The ClientHandler class implements the Callable interface and is responsible for handling client requests.
 * It reads messages from the client, processes them, and sends responses back to the client.
 */
class ClientHandler implements Callable<String> {
    private final Socket clientSocket;
    private final SQLEngine sqlEngine;
    private final int clientId;

    /**
     * Constructor for the ClientHandler class.
     * @param clientSocket the socket for the client connection
     * @param sqlEngine the SQL engine for database operations
     * @param clientId the ID of the client
     */
    public ClientHandler(Socket clientSocket, SQLEngine sqlEngine, int clientId) {
        this.clientSocket = clientSocket;
        this.sqlEngine = sqlEngine;
        this.clientId = clientId;
    }

    /**
     * The call method is the main method of the ClientHandler class.
     * It reads messages from the client, processes them, and sends responses back to the client.
     * @return a string indicating an error with the ClientHandler
     */
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
                    if (serverMessage.toLowerCase().contains("error"))
                        System.out.println("\nClient " + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + ": " + Color.ColorString(serverMessage.substring(0, serverMessage.indexOf(":")) + ": Error inside!", Color.ANSI_RED));
                    else if (serverMessage.toLowerCase().contains("no data") || serverMessage.toLowerCase().contains("nodata"))
                        System.out.println("\nClient " + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + ": " + Color.ColorString(serverMessage.substring(0, serverMessage.indexOf(":")) + ": No data inside!", Color.ANSI_YELLOW));
                    else
                        System.out.println("\nClient " + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + ": " + serverMessage.substring(0, serverMessage.indexOf(":")));
                } else
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
                    String[] loginResult = sqlEngine.loginToAccount(Integer.parseInt(clientId), username, password);

                    if (Boolean.parseBoolean(loginResult[0])) {
                        SendToClient.println("Logged successfully. Your user_id: " + loginResult[1]);
                    } else {
                        SendToClient.println("Wrong password.");
                    }
                } else if (serverMessage.startsWith("DOCTOR_LOGIN:")) {
                    String[] resources = serverMessage.substring(13).split(",");
                    String clientId = resources[0];
                    String username = resources[1];
                    String password = resources[2];
                    String[] loginResult = sqlEngine.loginToDoctor(Integer.parseInt(clientId), username, password);

                    if (Boolean.parseBoolean(loginResult[0])) {
                        SendToClient.println("Doctor logged successfully. Your user_id: " + loginResult[1]);
                    } else {
                        SendToClient.println("Wrong doctor password.");
                    }
                } else if (serverMessage.startsWith("GET_USER_DATA:")) {
                    String[] resources = serverMessage.substring(14).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] dataResult = sqlEngine.getData(Integer.parseInt(clientId), userId);
                    SendToClient.println(Arrays.toString(dataResult));

                } else if (serverMessage.startsWith("GET_DOCTOR_DATA:")) {
                    String[] resources = serverMessage.substring(16).split(",");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[] dataResult = sqlEngine.getDoctorData(Integer.parseInt(clientId), doctorId);
                    SendToClient.println(Arrays.toString(dataResult));

                } else if (serverMessage.startsWith("GET_CLINICS:")) {
                    String clientId = serverMessage.substring(12);

                    String[][] dataResult = sqlEngine.getClinics(Integer.parseInt(clientId));
                    SendToClient.println(Arrays.deepToString(dataResult));
                } else if (serverMessage.startsWith("GET_NOTIFICATIONS:")) {
                    String[] resources = serverMessage.substring(18).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] messagesResult = sqlEngine.getNotifications(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(messagesResult));
                } else if (serverMessage.startsWith("GET_MEDICAL_HISTORY")) {
                    String[] resources = serverMessage.substring(20).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] medicalHistoryResult = sqlEngine.getMedicalHistory(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(medicalHistoryResult));
                } else if (serverMessage.startsWith("GET_PRESSURE:")) {
                    String[] resources = serverMessage.substring(13).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] pressureResult = sqlEngine.getPressure(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(pressureResult));
                } else if (serverMessage.startsWith("GET_EREFERRAL:")) {
                    String[] resources = serverMessage.substring(14).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] eReferralResult = sqlEngine.getEReferral(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(eReferralResult));
                } else if (serverMessage.startsWith("GET_EPRESCRIPTION:")) {
                    String[] resources = serverMessage.substring(18).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] ePrescriptionResult = sqlEngine.getEPrescrition(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(ePrescriptionResult));
                } else if (serverMessage.startsWith("GET_RECOMMENDATION:")) {
                    String[] resources = serverMessage.substring(19).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] recommendationResult = sqlEngine.getRecommendation(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(recommendationResult));
                } else if (serverMessage.startsWith("GET_EXAMINATIONS:")) {
                    String[] resources = serverMessage.substring(17).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] messagesResult = sqlEngine.getExaminations(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(messagesResult));

                } else if (serverMessage.startsWith("GET_SETTINGS:")) {
                    String[] resources = serverMessage.substring(13).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] settingsResult = sqlEngine.getSettings(Integer.parseInt(clientId), userId);
                    SendToClient.println(Arrays.toString(settingsResult));
                } else if (serverMessage.startsWith("GET_DOCTOR_SETTINGS:")) {
                    String[] resources = serverMessage.substring(20).split(",");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[] settingsResult = sqlEngine.getDoctorSettings(Integer.parseInt(clientId), doctorId);
                    SendToClient.println(Arrays.toString(settingsResult));
                } else if (serverMessage.startsWith("GET_PATIENT:")) {
                    String[] resources = serverMessage.substring(12).split(",");
                    String clientId = resources[0];
                    String pesel = resources[1];

                    String[] patientResult = sqlEngine.getPatient(Integer.parseInt(clientId), pesel);
                    SendToClient.println(Arrays.toString(patientResult));

                } else if (serverMessage.startsWith("SET_SETTINGS:")) {
                    String[] resources = serverMessage.substring(13).split(",");
                    String clientId = resources[0];
                    String userId = resources[1];
                    String bmiSetting = resources[2];
                    String ageSetting = resources[3];
                    String dateSetting = resources[4];
                    String weightInChart_setting = resources[5];
                    String temperatureInChart_setting = resources[6];

                    String settingsChangedResult = sqlEngine.setSettings(Integer.parseInt(clientId), userId, bmiSetting, ageSetting, dateSetting, weightInChart_setting, temperatureInChart_setting);
                    SendToClient.println(settingsChangedResult);
                } else if (serverMessage.startsWith("PRESCRIBE_EPRESCRIPTION:")) {
                    String[] resources = serverMessage.substring(24).split(",");
                    String clientId = resources[0];
                    String medicines = resources[1];
                    String date = resources[2];
                    String doctorId = resources[3];
                    String pesel = resources[4];

                    String prescribingResult = sqlEngine.prescribeEPrescription(Integer.parseInt(clientId), medicines, date, doctorId, pesel);
                    SendToClient.println(prescribingResult);
                } else if (serverMessage.startsWith("SET_DOCTOR_SETTINGS:")) {
                    String[] resources = serverMessage.substring(20).split(",");
                    String clientId = resources[0];
                    String doctorId = resources[1];
                    String bmiSetting = resources[2];
                    String ageSetting = resources[3];
                    String dateSetting = resources[4];
                    String weightInChart_setting = resources[5];
                    String temperatureInChart_setting = resources[6];

                    String settingsChangedResult = sqlEngine.setDoctorSettings(Integer.parseInt(clientId), doctorId, bmiSetting, ageSetting, dateSetting, weightInChart_setting, temperatureInChart_setting);
                    SendToClient.println(settingsChangedResult);

                } else if (serverMessage.startsWith("CHECK_IF_USER_EXISTS:")) {
                    String[] resources = serverMessage.substring(21).split(",");
                    String clientId = resources[0];
                    String username = resources[1];
                    String email = resources[2];

                    String existingResult = sqlEngine.checkIfUserExists(Integer.parseInt(clientId), username, email);
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
            System.out.println(Color.ColorString("\nClient with id: ", Color.ANSI_PURPLE) + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + Color.ColorString(" disconnected.", Color.ANSI_PURPLE));
        }
        return "Error with ClientHandler!";
    }
}
