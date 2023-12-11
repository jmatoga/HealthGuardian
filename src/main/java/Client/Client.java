package Client;

import com.healthguardian.WindowApplication;
import utils.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static int user_id=-1;
    public static int clientId=-1;
    public static BufferedReader ReadFromServer;
    public static PrintWriter SendToServer;
    public static WindowApplication windowApplication = new WindowApplication();

    public static void main(String[] args) {
        Thread clientThread = new Thread(() -> {

            String serverAddress = "localhost";
            int serverPort = 12345;

            try (Socket clientSocket = new Socket(serverAddress, serverPort);
                 BufferedReader ReadFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter SendToServer = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                Client.ReadFromServer = ReadFromServer;
                Client.SendToServer = SendToServer;

                clientId = Integer.parseInt(ReadFromServer.readLine());
                System.out.println(Color.ColorString("ClientID: ", Color.ANSI_CYAN) + Color.ColorString("" + clientId, Color.ANSI_BLACK_BACKGROUND) + "\n");
                windowApplication.startWindow();

                try {
                    while (true) {
                        if (ReadFromServer.ready())
                            throw new RuntimeException("Something went wrong! Server is not ready to read.");
                    }
                } catch (RuntimeException e) {
                    System.err.println(e.getMessage());
                    System.exit(10);
                }

            } catch (java.net.ConnectException e) {
                System.out.println(Color.ColorString("ERROR! Server is not started!", Color.ANSI_RED));
                System.exit(-1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();
    }
}
