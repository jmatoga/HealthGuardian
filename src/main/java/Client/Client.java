package Client;

import utils.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The Client class is responsible for creating a connection with the server.
 * It also creates a thread for the client and contains the main method.
 */
public class Client {
    public static int user_id=-1;
    public static int clientId=-1;
    public static int doctor_id =-1;
    /**
     * BufferedReader to read data from the server
     */
    public static BufferedReader ReadFromServer;
    /**
     * PrintWriter to send data to the server
     */
    public static PrintWriter SendToServer;
    /**
     * ClientWindow instance for the client's window application
     */
    public static ClientWindow windowApplication = new ClientWindow();

    /**
     * The main method of the Client class.
     * It creates a new thread for the client, establishes a connection with the server,
     * and handles the communication with the server.
     * @param args command-line arguments
     */
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

    public static String getServerResponse(BufferedReader ReadFromServer) throws IOException {
        String serverAnswer = ReadFromServer.readLine();
        System.out.println(Color.ColorString("Server: ", Color.ANSI_YELLOW) + serverAnswer);
        return serverAnswer;
    }
}
