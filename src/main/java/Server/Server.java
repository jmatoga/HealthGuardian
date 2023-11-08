package Server;

import Client.Start;
import Scenes.ClientPanelController;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Client.Client;

public class Server {
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
                        System.out.println("Odebrano od klienta: " + inputLine);
                        out.println("Serwer: Otrzymałem twoją wiadomość - " + inputLine);
                    }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Client with id: " + clientId +" disconnected.");
            }
        }
    }

    public static boolean chceckUsernameAndPasswordInDataBase(String inputUsername, String inputPassword) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // Nawiązywanie połączenia z bazą danych
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
            System.out.println("Database connection succesful.");

            // Zapytanie SQL do sprawdzenia hasła
            String sql = "SELECT username,user_id FROM user_pass WHERE username = ? AND password_hash = SHA2(CONCAT(?, (SELECT salt FROM user_pass WHERE username=?)), 256)";

            // Utwórz prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inputUsername);  // username
            preparedStatement.setString(2, inputPassword);  // Wprowadzone hasło
            preparedStatement.setString(3, inputUsername);  // username

            // Wykonaj zapytanie
            resultSet = preparedStatement.executeQuery();

            //System.out.println(resultSet.next());
            //System.out.println(resultSet.next());
            //System.out.println(resultSet.next());

            // Sprawdź, czy zapytanie zwróciło wynik
            if (resultSet.next()) {
               // Client.setUser_id();
                 //  Client client = ;
//                setUser_id
//                =resultSet.getInt("user_id");
                System.out.println("Correct password.");
                return true;
            } else {
                System.out.println("Wrong password.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            // Zamykanie zasobów (ResultSet, Statement, Connection)
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error while closing resources: " + e.getMessage());
            }
        }
        return false;
    }
}
