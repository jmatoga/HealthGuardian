package IntegrationTests;

import ScenesControllers.ClientPanelController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import javafx.scene.Scene;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class ClientPanelControllerTest extends ApplicationTest {

    private ClientPanelController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ScenesControllers/ClientPanel.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        controller = loader.getController();
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testCalculateBMI() {
        // Symulacja danych wejściowych
        double weight = 70.0;
        double height = 170.0;

        // Oczekiwany wynik dla tych danych wejściowych
        double expectedBMI = 24.22;

        // Pobierz kontroler, jeśli potrzebujesz, aby przetestować metodę

        // Wywołaj metodę calculateBMI
        double result = controller.calculateBMI(weight, height);

        // Sprawdź czy oczekiwany wynik jest równy wynikowi z metody
        assertEquals(expectedBMI, result, 0.01); // Dopuszczalne odchylenie wyniku
        verifyThat("#bmiStatusLabel", hasText(String.format("%.2f", result)));

    }
}
