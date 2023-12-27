package Server;

import org.json.JSONObject;
import utils.Color;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static java.lang.System.exit;

/**
 * The Server class is responsible for setting up the server, accepting client connections, and handling client requests.
 * It uses a thread pool to handle multiple client connections concurrently.
 * It also uses a SQL engine for database operations.
 */
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
    private static final String secretKey = "HealthGuardianKey";

    // Singleton instance
    private static Server instance;

    private Server() throws IOException {
        byte[] key = generateKey(secretKey);

        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/Secret/passwords.json")));
        JSONObject jsonObject = new JSONObject(content);

        Server.DBhost = decrypt(jsonObject.getString("DBhost"), key);
        Server.DBPort = Integer.parseInt(Objects.requireNonNull(decrypt(jsonObject.getString("DBPort"), key)));
        Server.DBName = decrypt(jsonObject.getString("DBName"), key);
        Server.DBusername = decrypt(jsonObject.getString("DBusername"), key);
        Server.DBpassword = decrypt(jsonObject.getString("DBpassword"), key);

        // Create server socket
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server is listening on port: " + Color.ColorString("" + SERVER_PORT, Color.ANSI_CYAN));
        } catch (IOException e) {
            System.out.println(Color.ColorString("Server is already started on port " + SERVER_PORT + "!", Color.ANSI_RED));
            exit(-1);
        }

        try {
            // Create DB engine
            sqlEngine = new SQLEngine(DBhost, DBPort, DBName, DBusername, DBpassword);
            sqlEngine.checkDataBase();
        } catch (Exception e) {
            System.out.println(Color.ColorString(e.getMessage(), Color.ANSI_RED));
            e.printStackTrace();
            exit(-2);
        }
    }

    // Method to get the singleton instance
    public static Server getInstance() throws IOException {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    /**
     * Generates a key for encryption and decryption.
     *
     * @param secretKey the secret key string
     * @return the generated key
     */
    public static byte[] generateKey(String secretKey) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
            key = sha.digest(key);
            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts a string using a key.
     *
     * @param strToDecrypt the string to decrypt
     * @param key          the decryption key
     * @return the decrypted string
     */
    public static String decrypt(String strToDecrypt, byte[] key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
            return new String(decryptedBytes);
        } catch (Exception e) {
            System.out.println(Color.ColorString("Error while decrytping: " + e.toString(), Color.ANSI_RED));
            return null;
        }
    }

    /**
     * The main method of the Server class.
     * It sets up the server, accepts client connections, and handles client requests.
     *
     * @param args command-line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        int clientId = 0;
        instance = Server.getInstance();

        try {
            // Listen for client connections
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept new connection from Client
                System.out.println(Color.ColorString("\nNew client connected with id: ", Color.ANSI_YELLOW) + Color.ColorString("" + ++clientId, Color.ANSI_BLACK_BACKGROUND));
                //System.out.println("\nNew client connected with id: " + clientSocket.getInetAddress().getHostAddress());

                // Submit new task to thread pool
                FutureTask<String> task = new FutureTask<>(new ClientHandler(clientSocket, sqlEngine, clientId));
                executorService.submit(task);
            }
        } finally {
            serverSocket.close();
            executorService.shutdown();
            System.out.println("Server stopped successfully.");
        }
    }
}