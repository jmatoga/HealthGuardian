package Client;

import com.healthguardian.HelloApplication;

import javax.swing.*;

public class Start {
    public static void main(String[] args) {
        Client client = new Client();
        HelloApplication helloApplication = new HelloApplication();

        // Uruchom klienta w oddzielnym wątku
        Thread clientThread = new Thread(() -> {
            client.connectToServer(args);
        });
        clientThread.start();

        // Wyświetl okno klienta
        SwingUtilities.invokeLater(() -> {
            helloApplication.main(args);
            System.exit(0);
        });
    }
}

