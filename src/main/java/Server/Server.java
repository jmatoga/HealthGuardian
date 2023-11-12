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
    private static final String DBhost = "5he.h.filess.io";
    private static final int DBPort = 3307;
    private static final String DBName = "HealthGuardian_alloweager";
    private static final String DBusername = "HealthGuardian_alloweager";
    private static final String DBpassword = "2c2673f30c0d5912c63a30445aeb6dde46e713d0";
    private static SQLEngine sqlEngine;

    public static void main(String[] args) throws IOException {
        int clientId = 0;

        // Create server socket
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server is listening on port: " + SERVER_PORT);
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
            System.out.println("\nNew client connected with id: " + ++clientId);
            //System.out.println("\nNew client connected with id: " + clientSocket.getInetAddress().getHostAddress());

            // Submit new task to thread pool
            FutureTask<String> task = new FutureTask<>(new ClientHandler(clientSocket, sqlEngine, clientId));
            executorService.submit(task);
        }
    }
}
