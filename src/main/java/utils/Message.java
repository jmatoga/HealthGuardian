package utils;

import java.io.PrintWriter;

/**
 * The Message class provides methods for sending different types of messages to the server.
 * Each method takes a PrintWriter and a message as parameters.
 * The PrintWriter is used to send the message to the server.
 * The message is a string that contains the content of the message.
 */
public class Message {
    /**
     * Sends an input message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendInputMessage(PrintWriter writer, String message) {
        writer.println("SEND:" + message);
    }

    /**
     * Sends an exit message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendExitMessage(PrintWriter writer, String message) {
        writer.println("EXIT:" + message);
    }

    /**
     * Sends a print message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendPrintMessage(PrintWriter writer, String message) {
        writer.println("PRINT:" + message);
    }

    /**
     * Sends a login message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendLoginMessage(PrintWriter writer, String message) {
        writer.println("LOGIN:" + message);
    }

    /**
     * Sends a register message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendRegisterMessage(PrintWriter writer, String message) {
        writer.println("REGISTER:" + message);
    }

    /**
     * Sends a get name message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetNameMessage(PrintWriter writer, String message) { writer.println("GET_USER_DATA:" + message);}

    /**
     * Sends a check if user exists message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void checkIfUserExists(PrintWriter writer, String message) { writer.println("CHECK_IF_USER_EXISTS:" + message); }

    /**
     * Sends a check one time code message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void checkOneTimeCode(PrintWriter writer, String message) {
        writer.println("CHECK_ONE_TIME_CODE:" + message);
    }

    /**
     * Sends an update user basic data message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void updateUserBasicData(PrintWriter writer, String message) {
        writer.println("UPDATE_USER_BASIC_DATA:" + message);
    }

    /**
     * Sends a get settings message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetSettingsMessage(PrintWriter writer, String message) {
        writer.println("GET_SETTINGS:" + message);
    }

    /**
     * Sends a set settings message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendSetSettingsMessage(PrintWriter writer, String message) {
        writer.println("SET_SETTINGS:" + message);
    }

    /**
     * Sends a get clinics message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetClinicsMessage(PrintWriter writer, String message) {
        writer.println("GET_CLINICS:" + message);
    }

    /**
     * Sends a get messages message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetNotificationsMessage(PrintWriter writer, String message) {
        writer.println("GET_NOTIFICATIONS:" + message);
    }

    /**
     * Sends a get examinations message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetExaminationsMessage(PrintWriter writer, String message) {
        writer.println("GET_EXAMINATIONS:" + message);
    }
    public void sendGetPressureMessage(PrintWriter writer, String message) {
        writer.println("GET_PRESSURE:" + message);
    }
    public void sendGetMedicalHistoryMessage(PrintWriter writer, String message) {
        writer.println("GET_MEDICAL_HISTORY:" + message);
    }
}