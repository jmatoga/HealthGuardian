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
}