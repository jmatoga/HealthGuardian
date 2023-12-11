package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import utils.Color;

public class Server {
    // Server properties
    private static final int SERVER_PORT = 12345;
    private static final ExecutorService executorService = Executors.newCachedThreadPool(); // Unlimited threads
    private static ServerSocket serverSocket;

    // DB properties
    private static String DBhost;
    private static int DBPort;
    private static String DBName;
    private static String DBusername;
    private static String DBpassword;
    private static SQLEngine sqlEngine;

    public static void main(String[] args) throws IOException {
        int clientId = 0;

        String fileName = "src/main/resources/Secret/passwords.txt";
        // Read passwords from file
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            Server.DBhost = br.readLine();
            Server.DBPort = Integer.parseInt(br.readLine());
            Server.DBName = br.readLine();
            Server.DBusername = br.readLine();
            Server.DBpassword = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create server socket
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server is listening on port: " + Color.ColorString("" + SERVER_PORT, Color.ANSI_CYAN));
        } catch (IOException e) {
            System.out.println("Server could not listen on port " + SERVER_PORT);
            System.exit(-1);
        }

        try {
            // Create DB engine
            sqlEngine = new SQLEngine(DBhost, DBPort, DBName, DBusername, DBpassword);
            System.out.println(Color.ColorString("DB engine created", Color.ANSI_GREEN));
        } catch (Exception e) {
            System.out.println(Color.ColorString(e.getMessage(), Color.ANSI_RED));
            e.printStackTrace();
            System.exit(-2);
        }

        // Listen for client connections
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Accept new connection from Client
            System.out.println("\nNew client connected with id: " + Color.ColorString("" + ++clientId, Color.ANSI_BLACK_BACKGROUND));
            //System.out.println("\nNew client connected with id: " + clientSocket.getInetAddress().getHostAddress());

            // Submit new task to thread pool
            FutureTask<String> task = new FutureTask<>(new ClientHandler(clientSocket, sqlEngine, clientId));
            executorService.submit(task);
        }
    }
}
