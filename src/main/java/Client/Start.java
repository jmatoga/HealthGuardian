package Client;

import com.healthguardian.WindowApplication;

public class Start {
    public static void main(String[] args) {
        Client client = new Client();
        WindowApplication windowApplication = new WindowApplication();

        // Uruchom klienta w oddzielnym wątku
        Thread clientThread = new Thread(() -> {
            client.main(args);
            // Dodaj logikę obsługi klienta
        });
        clientThread.start();

        // Wyświetl okno klienta
        //SwingUtilities.invokeLater(() -> {
            windowApplication.startWindow();
            //System.exit(0);
        //});
    }
}
