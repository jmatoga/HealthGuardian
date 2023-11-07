package Server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        int port = 12345;
        ServerSocket serverSocket;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serwer nasłuchuje na porcie " + port + "...");

            ExecutorService executor = Executors.newCachedThreadPool(); // Dowolna liczba wątków

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Akceptowanie połączenia od klienta
                System.out.println("Nowy klient połączony.");

                // Obsługa klienta w nowym wątku
                executor.execute(new ClientHandler(clientSocket));

                // Nawiązywanie połączenia z bazą danych
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
                System.out.println("Połączenie z bazą danych zostało nawiązane.");

                // Tworzenie obiektu Statement
                statement = connection.createStatement();

                // Wykonanie zapytania SELECT
                String query = "SELECT * FROM client"; // Zastąp "tabela" nazwą tabeli, którą chcesz zapytać
                resultSet = statement.executeQuery(query);

                // Przetwarzanie wyników zapytania
                while (resultSet.next()) {
                    // Tutaj możesz odczytywać dane z wyników zapytania
                    int id = resultSet.getInt("id"); // Przykład: pobranie kolumny "id"
                    String nazwa = resultSet.getString("aaa"); // Przykład: pobranie kolumny "nazwa"
                    System.out.println("ID: " + id + ", Nazwa: " + nazwa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Błąd podczas wykonywania zapytania SELECT: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Zamykanie zasobów (ResultSet, Statement, Connection)
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Błąd podczas zamykania zasobów: " + e.getMessage());
            }
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket; // idk czy dobrze

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
            e.printStackTrace();
        }
    }
}