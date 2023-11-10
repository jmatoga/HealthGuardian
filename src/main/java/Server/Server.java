package Server;

import Client.Start;
import Scenes.ClientPanelController;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Client.Client;
import Scenes.LogInController;

public class Server {
    public static int d;
    public static void main(String[] args) {
        int port = 12345;
        int clientId = 0;
        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is listening on port " + port + "...");

            ExecutorService executor = Executors.newCachedThreadPool(); // Dowolna liczba wątków

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Akceptowanie połączenia od klienta
                System.out.println("\nNew client connected with id: " + ++clientId);

                // Obsługa klienta w nowym wątku
                executor.execute(new ClientHandler(clientSocket, clientId));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientId;

        public ClientHandler(Socket clientSocket, int clientId) {
            this.clientSocket = clientSocket;
            this.clientId = clientId;
        }

        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        out.println("Your ClientID: " + clientId);
                        System.out.println("Odebrano od klienta: " + inputLine);
                        out.println("Serwer: Otrzymałem twoją wiadomość - " + inputLine);
                    }
                    while (true) {
                        Thread.sleep(5000);
                        System.out.println(d);
                        if(d==1) {
                            System.out.println("dss");
                            d=0;
                        }
                    }
            } catch (IOException e) {
                e.printStackTrace();
                // System.out.println("Client with id: " + Client.clientId +" disconnected.");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String[] chceckUsernameAndPasswordInDataBase(int clientID, String inputUsername, String inputPassword) {
        String[] returnStatement = {"false", "-1"};
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(clientID);

            String sql = "SELECT username,user_id FROM user_pass WHERE username = ? AND password_hash = SHA2(CONCAT(?, (SELECT salt FROM user_pass WHERE username=?)), 256)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inputUsername);  // username
            preparedStatement.setString(2, inputPassword);  // password
            preparedStatement.setString(3, inputUsername);  // username

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if username and password exist in database in same user
                System.out.println("Correct password.");
                returnStatement[0] = "true";
                returnStatement[1] = String.valueOf(resultSet.getInt("user_id"));

                return returnStatement;
            } else {
                System.out.println("Wrong password.");
                returnStatement[0] = "false";
                returnStatement[1] = "-1";

                return returnStatement;
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    private static Connection connectToDataBase(int clientId) throws SQLException {
        // LINK TO ADMIN ACCESS: https://client.filess.io/?username=HealthGuardian_alloweager&server=5he.h.filess.io:3307&db=HealthGuardian_alloweager&password=2c2673f30c0d5912c63a30445aeb6dde46e713d0&driver=server

        // old (local) database //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
        Connection connection = DriverManager.getConnection("jdbc:mysql://5he.h.filess.io:3307/HealthGuardian_alloweager", "HealthGuardian_alloweager", "2c2673f30c0d5912c63a30445aeb6dde46e713d0");
        System.out.println("Database connection successful. (For ClientID: " + clientId + ")");
        return connection;
    }

    private static void disconnectFromDataBase(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        // Closing resources (ResultSet, PreparedStatement, Connection)
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
            System.out.println("Database connection closed successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while closing resources: " + e.getMessage());
        }
    }

    public static void e() {
        d = 1;
    }
}
