package Client;

import com.healthguardian.HelloApplication;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    int user_id=-1;
    public static void connectToServer(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String serverAddress = "localhost";
        int serverPort = 12345;
        try (Socket clientSocket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true))
             {

            out.println("Cześć, serwer!");
            String response = in.readLine();
            System.out.println("Odebrano od serwera: " + response);

//            System.out.println("\n>>>> ");
//            String text = scanner.nextLine();
//            System.out.printf(text);
//
//            while(true) {
//                Thread.sleep(5000);
//                System.out.println("d");
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
   }
}