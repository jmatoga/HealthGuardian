package Server;

import utils.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.lang.System.exit;

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
     *
     * @param clientSocket the socket for the client connection
     * @param sqlEngine    the SQL engine for database operations
     * @param clientId     the ID of the client
     */
    public ClientHandler(Socket clientSocket, SQLEngine sqlEngine, int clientId) {
        this.clientSocket = clientSocket;
        this.sqlEngine = sqlEngine;
        this.clientId = clientId;
    }

    /**
     * The call method is the main method of the ClientHandler class.
     * It reads messages from the client, processes them, and sends responses back to the client.
     *
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
                    String[] resources = serverMessage.substring(6).split("#/#");
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
                    String[] resources = serverMessage.substring(13).split("#/#");
                    String clientId = resources[0];
                    String username = resources[1];
                    String password = resources[2];
                    String[] loginResult = sqlEngine.loginToDoctor(Integer.parseInt(clientId), username, password);

                    if (Boolean.parseBoolean(loginResult[0])) {
                        SendToClient.println("Doctor logged successfully. Your doctor_id: " + loginResult[1]);
                    } else {
                        SendToClient.println("Wrong doctor password.");
                    }
                } else if (serverMessage.startsWith("ADMIN_LOGIN:")) {
                    String[] resources = serverMessage.substring(12).split("#/#");
                    String clientId = resources[0];
                    String adminId = resources[1];
                    String password = resources[2];
                    String[] loginResult = sqlEngine.loginToAdmin(Integer.parseInt(clientId), Integer.parseInt(adminId), password);

                    if (Boolean.parseBoolean(loginResult[0])) {
                        SendToClient.println("Admin logged successfully. Your admin_id: " + loginResult[1]);
                    } else {
                        SendToClient.println("Wrong admin password.");
                    }
                } else if (serverMessage.startsWith("GET_USER_DATA:")) {
                    String[] resources = serverMessage.substring(14).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] dataResult = sqlEngine.getData(Integer.parseInt(clientId), userId);
                    SendToClient.println(Arrays.toString(dataResult));
                } else if (serverMessage.startsWith("CREATE_NEW_ONE_TIME_CODES:")) {
                    String[] resources = serverMessage.substring(26).split("#/#");
                    String clientId = resources[0];

                    String result = sqlEngine.createNewOneTimeCodes(Integer.parseInt(clientId));
                    SendToClient.println(result);
                } else if (serverMessage.startsWith("GET_DOCTOR_DATA:")) {
                    String[] resources = serverMessage.substring(16).split("#/#");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[] dataResult = sqlEngine.getDoctorData(Integer.parseInt(clientId), doctorId);
                    SendToClient.println(Arrays.toString(dataResult));

                } else if (serverMessage.startsWith("GET_CLINICS:")) {
                    String clientId = serverMessage.substring(12);

                    String[][] dataResult = sqlEngine.getClinics(Integer.parseInt(clientId));
                    SendToClient.println(Arrays.deepToString(dataResult));
                } else if (serverMessage.startsWith("GET_NOTIFICATIONS:")) {
                    String[] resources = serverMessage.substring(18).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] messagesResult = sqlEngine.getNotifications(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(messagesResult));
                } else if (serverMessage.startsWith("GET_DOCTOR_NOTIFICATIONS:")) {
                    String[] resources = serverMessage.substring(25).split("#/#");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[][] messagesResult = sqlEngine.getDoctorNotifications(Integer.parseInt(clientId), Integer.parseInt(doctorId));
                    SendToClient.println(Arrays.deepToString(messagesResult));
                } else if (serverMessage.startsWith("GET_LAST_BLOOD_PRESSURE_CHECK:")) {
                    String[] resources = serverMessage.substring(30).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String messagesResult = sqlEngine.getLastBloodPressureCheck(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(messagesResult);
                } else if (serverMessage.startsWith("GET_MEDICAL_HISTORY:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] medicalHistoryResult = sqlEngine.getMedicalHistory(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(medicalHistoryResult));
                } else if (serverMessage.startsWith("GET_DOCTOR_MEDICAL_HISTORY:")) {
                    String[] resources = serverMessage.substring(27).split("#/#");
                    String clientId = resources[0];
                    String pesel = resources[1];

                    String[][] medicalHistoryResult = sqlEngine.getDoctorMedicalHistory(Integer.parseInt(clientId), pesel);
                    SendToClient.println(Arrays.deepToString(medicalHistoryResult));
                } else if (serverMessage.startsWith("GET_DOCTOR_DOCUMENTATIONS:")) {
                    String[] resources = serverMessage.substring(26).split("#/#");
                    String clientId = resources[0];
                    String pesel = resources[1];

                    String[][] documentationsResult = sqlEngine.getDocumentations(Integer.parseInt(clientId), pesel);
                    SendToClient.println(Arrays.deepToString(documentationsResult));
                } else if (serverMessage.startsWith("GET_PRESSURE:")) {
                    String[] resources = serverMessage.substring(13).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] pressureResult = sqlEngine.getPressure(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(pressureResult));
                } else if (serverMessage.startsWith("GET_EREFERRAL:")) {
                    String[] resources = serverMessage.substring(14).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] eReferralResult = sqlEngine.getEReferral(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(eReferralResult));
                } else if (serverMessage.startsWith("GET_FINDINGS:")) {
                    String[] resources = serverMessage.substring(13).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] findingsResult = sqlEngine.getFindings(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(findingsResult));
                } else if (serverMessage.startsWith("GET_DOCTOR_FINDINGS:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
                    String clientId = resources[0];
                    String pesel = resources[1];

                    String[][] findingsResult = sqlEngine.getDoctorFindings(Integer.parseInt(clientId), pesel);
                    SendToClient.println(Arrays.deepToString(findingsResult));
                } else if (serverMessage.startsWith("GENERATE_REPORT:")) {
                    String[] resources = serverMessage.substring(16).split("#/#");
                    String clientId = resources[0];

                    ArrayList<String> reportResult = sqlEngine.generateReport(Integer.parseInt(clientId));
                    SendToClient.println(reportResult);
                } else if (serverMessage.startsWith("GET_EPRESCRIPTION:")) {
                    String[] resources = serverMessage.substring(18).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] ePrescriptionResult = sqlEngine.getEPrescrition(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(ePrescriptionResult));
                } else if (serverMessage.startsWith("GET_RECOMMENDATION:")) {
                    String[] resources = serverMessage.substring(19).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] recommendationResult = sqlEngine.getRecommendation(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(recommendationResult));
                } else if (serverMessage.startsWith("GET_SMI:")) {
                    String[] resources = serverMessage.substring(8).split("#/#");
                    String clientId = resources[0];
                    String smiCode = resources[1];
                    String pesel = resources[2];

                    String[] smiResult = sqlEngine.getSMI(Integer.parseInt(clientId), Integer.parseInt(smiCode), pesel);
                    SendToClient.println(Arrays.toString(smiResult));
                } else if (serverMessage.startsWith("GET_EXAMINATIONS:")) {
                    String[] resources = serverMessage.substring(17).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[][] messagesResult = sqlEngine.getExaminations(Integer.parseInt(clientId), Integer.parseInt(userId));
                    SendToClient.println(Arrays.deepToString(messagesResult));

                } else if (serverMessage.startsWith("GET_SETTINGS:")) {
                    String[] resources = serverMessage.substring(13).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];

                    String[] settingsResult = sqlEngine.getSettings(Integer.parseInt(clientId), userId);
                    SendToClient.println(Arrays.toString(settingsResult));
                } else if (serverMessage.startsWith("GET_DOCTOR_SETTINGS:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[] settingsResult = sqlEngine.getDoctorSettings(Integer.parseInt(clientId), doctorId);
                    SendToClient.println(Arrays.toString(settingsResult));
                } else if (serverMessage.startsWith("GET_PATIENT:")) {
                    String[] resources = serverMessage.substring(12).split("#/#");
                    String clientId = resources[0];
                    String pesel = resources[1];

                    String[] patientResult = sqlEngine.getPatient(Integer.parseInt(clientId), pesel);
                    SendToClient.println(Arrays.toString(patientResult));

                } else if (serverMessage.startsWith("SET_SETTINGS:")) {
                    String[] resources = serverMessage.substring(13).split("#/#");
                    String clientId = resources[0];
                    String userId = resources[1];
                    String bmiSetting = resources[2];
                    String ageSetting = resources[3];
                    String dateSetting = resources[4];
                    String weightInChart_setting = resources[5];
                    String temperatureInChart_setting = resources[6];
                    String background_setting = resources[7];

                    String settingsChangedResult = sqlEngine.setSettings(Integer.parseInt(clientId), userId, bmiSetting, ageSetting, dateSetting, weightInChart_setting, temperatureInChart_setting, background_setting);
                    SendToClient.println(settingsChangedResult);
                } else if (serverMessage.startsWith("PRESCRIBE_EPRESCRIPTION:")) {
                    String[] resources = serverMessage.substring(24).split("#/#");
                    String clientId = resources[0];
                    String medicines = resources[1];
                    String date = resources[2];
                    String doctorId = resources[3];
                    String pesel = resources[4];

                    String prescribingResult = sqlEngine.prescribeEPrescription(Integer.parseInt(clientId), medicines, date, doctorId, pesel);
                    SendToClient.println(prescribingResult);
                } else if (serverMessage.startsWith("PRESCRIBE_EREFERRAL:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
                    String clientId = resources[0];
                    String eReferralName = resources[1];
                    String date = resources[2];
                    String doctorId = resources[3];
                    String pesel = resources[4];

                    String prescribingResult = sqlEngine.prescribeEReferral(Integer.parseInt(clientId), eReferralName, date, doctorId, pesel);
                    SendToClient.println(prescribingResult);
                } else if (serverMessage.startsWith("ADD_MEDICAL_HISTORY:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
                    String clientId = resources[0];
                    String medicalCase = resources[1];
                    String ICD10FirstLetter = resources[2];
                    String ICD10Code = resources[3];
                    String pesel = resources[4];

                    String addingResult = sqlEngine.addMedicalHistory(Integer.parseInt(clientId), medicalCase, ICD10FirstLetter, ICD10Code, pesel);
                    SendToClient.println(addingResult);
                } else if (serverMessage.startsWith("ADD_DOCUMENTATION:")) {
                    String[] resources = serverMessage.substring(18).split("#/#");
                    String clientId = resources[0];
                    String interview = resources[1];
                    String physicalExamination = resources[2];
                    String ICD10Code = resources[3];
                    String recommendationId = resources[4];
                    String pesel = resources[5];
                    String date = resources[6];
                    String doctorId = resources[7];

                    String addingResult = sqlEngine.addDocumentation(Integer.parseInt(clientId), interview, physicalExamination, ICD10Code, recommendationId, pesel, date, doctorId);
                    SendToClient.println(addingResult);
                } else if (serverMessage.startsWith("DELETE_DOCUMENTATION:")) {
                    String[] resources = serverMessage.substring(21).split("#/#");
                    String clientId = resources[0];
                    String documentationId = resources[1];

                    String addingResult = sqlEngine.deleteDocumentation(Integer.parseInt(clientId), documentationId);
                    SendToClient.println(addingResult);
                } else if (serverMessage.startsWith("DELETE_MEDICAL_HISTORY:")) {
                    String[] resources = serverMessage.substring(23).split("#/#");
                    String clientId = resources[0];
                    String medicalHistoryId = resources[1];

                    String addingResult = sqlEngine.deleteMedicalHistory(Integer.parseInt(clientId), medicalHistoryId);
                    SendToClient.println(addingResult);
                } else if (serverMessage.startsWith("ADD_RECOMMENDATION:")) {
                    String[] resources = serverMessage.substring(19).split("#/#");
                    String clientId = resources[0];
                    String diet = resources[1];
                    String medicines = resources[2];
                    String nextCheckUpDate = resources[3];
                    String nextCheckUpName = resources[4];
                    String additionalInformation = resources[5];
                    String date = resources[6];
                    String doctor_id = resources[7];
                    String pesel = resources[8];

                    String addingResult = sqlEngine.addRecommendation(Integer.parseInt(clientId), diet, medicines, nextCheckUpDate, nextCheckUpName, additionalInformation, date, doctor_id, pesel);
                    SendToClient.println(addingResult);
                } else if (serverMessage.startsWith("SET_DOCTOR_SETTINGS:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
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
                    String[] resources = serverMessage.substring(21).split("#/#");
                    String clientId = resources[0];
                    String username = resources[1];
                    String email = resources[2];

                    String existingResult = sqlEngine.checkIfUserExists(Integer.parseInt(clientId), username, email);
                    SendToClient.println("EXISTING RESULT:" + existingResult);
                } else if (serverMessage.startsWith("CHECK_IF_DOCTOR_EXISTS:")) {
                    String[] resources = serverMessage.substring(23).split("#/#");
                    String clientId = resources[0];
                    String username = resources[1];
                    String email = resources[2];
                    String clinic = resources[3];

                    String existingResult = sqlEngine.checkIfDoctorExists(Integer.parseInt(clientId), username, email, clinic);
                    SendToClient.println("EXISTING RESULT:" + existingResult);

                } else if (serverMessage.startsWith("CHECK_ONE_TIME_CODE:")) {
                    String[] resources = serverMessage.substring(20).split("#/#");
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
                } else if (serverMessage.startsWith("ADD_NEW_DOCTOR:")) {
                    String[] resources = serverMessage.substring(15).split("#/#");
                    String clientId = resources[0];
                    String firstname = resources[1];
                    String lastname = resources[2];
                    String phoneNumber = resources[3];
                    String email = resources[4];
                    String gender = resources[5];
                    String profession = resources[6];
                    String username = resources[7];
                    String password = resources[8];
                    String clinicId = resources[9];

                    String addingResult = sqlEngine.addNewDoctor(Integer.parseInt(clientId), firstname, lastname, phoneNumber, email, gender, profession, username, password, clinicId);
                    SendToClient.println(addingResult);

                } else if (serverMessage.startsWith("UPDATE_USER_BASIC_DATA:")) {
                    String[] resources = serverMessage.substring(23).split("#/#");
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

                } else if (serverMessage.startsWith("ADD_SMI:")) {
                    String[] resources = serverMessage.substring(8).split("#/#");
                    String clientId = resources[0];
                    String what_hurts_you = resources[1];
                    String pain_symptoms = resources[2];
                    String other_symptoms = resources[3];
                    String symptoms_other_symptoms = resources[4];
                    String medicines = resources[5];
                    String extent_of_pain = resources[6];
                    String when_the_pain_started = resources[7];
                    String temperature = resources[8];
                    String additional_description = resources[9];
                    String result_smi = resources[10];
                    String smi_date = resources[11];
                    String user_id = resources[12];

                    String addingResult = sqlEngine.addShortMedicalInterview(Integer.parseInt(clientId), what_hurts_you, pain_symptoms, other_symptoms, symptoms_other_symptoms, medicines, extent_of_pain, when_the_pain_started, temperature, additional_description, result_smi, smi_date, Integer.parseInt(user_id));
                    SendToClient.println(addingResult);

                } else if (serverMessage.startsWith("ADD_SMI_EREFERRAL:")) {
                    String[] resources = serverMessage.substring(18).split("#/#");
                    String clientId = resources[0];
                    String referral_id = resources[1];
                    String barcode = resources[2];
                    String date_of_issue = resources[3];
                    String e_referral_code = resources[4];
                    String referral_name = resources[5];
                    String doctor_id = resources[6];
                    String user_id = resources[7];

                    String addingResult = sqlEngine.addSMIEreferral(Integer.parseInt(clientId), referral_id, barcode, date_of_issue, e_referral_code, referral_name, Integer.parseInt(doctor_id), Integer.parseInt(user_id));
                    SendToClient.println(addingResult);

                } else if (serverMessage.startsWith("GET_ECONTACT:")) {
                    String[] resources = serverMessage.substring(13).split("#/#");
                    String clientId = resources[0];
                    String user_id = resources[1];

                    String[][] eContactResult = sqlEngine.getEContact(Integer.parseInt(clientId), Integer.parseInt(user_id));
                    SendToClient.println(Arrays.deepToString(eContactResult));

                } else if (serverMessage.startsWith("GET_EXAMINATIONS_FOR_TODAY:")) {
                    String[] resources = serverMessage.substring(27).split("#/#");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[][] messagesResult = sqlEngine.getExaminationsForToday(Integer.parseInt(clientId), Integer.parseInt(doctorId));
                    SendToClient.println(Arrays.deepToString(messagesResult));

                } else if (serverMessage.startsWith("ADD_LINK:")) {
                    String[] resources = serverMessage.substring(9).split("#/#");
                    String clientId = resources[0];
                    String examination_nr = resources[1];
                    String link = resources[2];

                    String addingResult = sqlEngine.addExaminationLink(Integer.parseInt(clientId), examination_nr, link);
                    SendToClient.println(addingResult);

                } else if (serverMessage.startsWith("GET_DOCTOR_EXAMINATIONS:")) {
                    String[] resources = serverMessage.substring(24).split("#/#");
                    String clientId = resources[0];
                    String doctorId = resources[1];

                    String[][] messagesResult = sqlEngine.getDoctorExaminations(Integer.parseInt(clientId), Integer.parseInt(doctorId));
                    SendToClient.println(Arrays.deepToString(messagesResult));

                } else if (serverMessage.startsWith("GET_DOCTORS_EXAM:")) {
                    String[] resources = serverMessage.substring(17).split("#/#");
                    String clientId = resources[0];

                    String[][] messagesResult = sqlEngine.getDoctorsExam(Integer.parseInt(clientId));
                    SendToClient.println(Arrays.deepToString(messagesResult));

                } else if (serverMessage.startsWith("GET_HOURS_EXAM:")) {
                    String[] resources = serverMessage.substring(15).split("#/#");
                    String clientId = resources[0];
                    String doctorId = resources[1];
                    String date = resources[2];

                    String messagesResult = sqlEngine.getHoursExam(Integer.parseInt(clientId), Integer.parseInt(doctorId), date);
                    SendToClient.println(messagesResult);

                } else if (serverMessage.startsWith("MAKE_NEW_EXAMINATIONS:")) {
                    String[] resources = serverMessage.substring(22).split("#/#");
                    String clientId = resources[0];
                    String name =resources[1];
                    String date = resources[2];
                    String shortDescription = resources[3];
                    String doctorId = resources[4];
                    String userId = resources[5];

                    String messagesResult = sqlEngine.makeNewExaminations(Integer.parseInt(clientId), name, date, shortDescription, Integer.parseInt(doctorId),  Integer.parseInt(userId));
                    SendToClient.println(messagesResult);

                } else {
                    System.out.println(Color.ColorString("Something went wrong in ClientHandler (probably wrong substring number)", Color.ANSI_RED));
                    exit(-100);
                }
            }
        } catch (IOException e) {
            System.out.println(Color.ColorString("\nClient with id: ", Color.ANSI_PURPLE) + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + Color.ColorString(" disconnected.", Color.ANSI_PURPLE));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "Error with ClientHandler!";
    }
}
