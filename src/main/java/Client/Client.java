package Client;

import Scenes.LogInController;
import Scenes.SignInController;
import com.healthguardian.WindowApplication;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static int user_id=-1;
    public static int clientId=-1;
    public void connectToServer() {
        String serverAddress = "localhost";
        int serverPort = 12345;

        try (Socket clientSocket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            out.println("Hi server!");
            String response = in.readLine();
            clientId = Integer.parseInt(response.substring(15));
            System.out.println("ClientID: " + clientId);
            System.out.println(in.readLine());

            //LogInController.getClientId();

//            System.out.println("\n>>>> ");
//            String text = scanner.nextLine();
//            System.out.printf(text);
//
            while(true) {
                Thread.sleep(5000);
                System.out.println("d");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }
}