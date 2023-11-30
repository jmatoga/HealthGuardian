package Client;

import ScenesControllers.LogInController;
import utils.Color;
import utils.Message;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
    public static int user_id=-1;
    public static int clientId=-1;
    public static BufferedReader ReadFromServer;
    public static PrintWriter SendToServer;
    private static final Lock lock = new ReentrantLock();
    public static String lastServerMessage = "a";
    private static final Condition condition = lock.newCondition();

    public static synchronized void reader(BufferedReader ReadFromServer) {
        try {
            while (true) {
                String serverMessage = ReadFromServer.readLine();
               // String serverMessage = rreader(ReadFromServer, false);
                //System.out.println("a");

                if (serverMessage != null) {
                    lock.lock();
                    condition.await();
                    System.out.println("Client: Received message from server: " + serverMessage);
                    lastServerMessage = serverMessage;
                    condition.signalAll();
                    lock.unlock();
                }
                else
                    throw new RuntimeException("Something went wrong! Server sent a null message.");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    public static synchronized String rreader(BufferedReader ReadFromServer, boolean ifTrue) throws IOException {
//        try {
//            //lock.lock();
//            if(ifTrue) {
//                rreader(ReadFromServer, false).wait();
//                return ReadFromServer.readLine();
//            }
//            else
//                return "";
//
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            //lock.unlock();
//            rreader(ReadFromServer, false).notifyAll();
//        }
//    }
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 12345;

        try (Socket clientSocket = new Socket(serverAddress, serverPort);
             BufferedReader ReadFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter SendToServer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            Client.ReadFromServer = ReadFromServer;
            Client.SendToServer = SendToServer;

            clientId = Integer.parseInt(ReadFromServer.readLine());
            System.out.println("ClientID: " + clientId);

            reader(ReadFromServer);

        }  catch (java.net.ConnectException e) {
            System.out.println(Color.ColorString("ERROR! Server is not started!", Color.ANSI_RED));
            System.exit(-1);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}