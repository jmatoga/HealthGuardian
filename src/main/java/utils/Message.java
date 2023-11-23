package utils;

import java.io.PrintWriter;

public class Message {
    public void sendInputMessage(PrintWriter writer, String message) {
        writer.println("SEND:" + message);
    }

    public void sendExitMessage(PrintWriter writer, String message) {
        writer.println("EXIT:" + message);
    }

    public void sendPrintMessage(PrintWriter writer, String message) {
        writer.println("PRINT:" + message);
    }

    public void sendLoginMessage(PrintWriter writer, String message) {
        writer.println("LOGIN:" + message);
    }

    public void sendRegisterMessage(PrintWriter writer, String message) {
        writer.println("REGISTER:" + message);
    }

    public void sendGetNameMessage(PrintWriter writer, String message) { writer.println("GET_USER_DATA:" + message);}

    public void checkIfUserExists(PrintWriter writer, String message) { writer.println("CHECK_IF_USER_EXISTS:" + message); }

    public void checkOneTimeCode(PrintWriter writer, String message) {
        writer.println("CHECK_ONE_TIME_CODE:" + message);
    }
    public void updateUserBasicData(PrintWriter writer, String message) {
        writer.println("UPDATE_USER_BASIC_DATA:" + message);
    }
}