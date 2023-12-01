package ScenesControllers;//package ScenesControllers;

import ScenesControllers.ClientPanelController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class CPCTEST extends ApplicationTest {
    public ClientPanelController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FxToolkit.registerPrimaryStage();
        // Tu można dodać kod do tworzenia sceny i ładowania kontrolera
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientPanelController.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testCalculateBMI_LowWeightLowHeight() {
        // Przygotowanie stanu początkowego, np. ustawienie niskiej wagi i wzrostu
        double weight = 40.0;
        double height = 140.0;

        // Wywołanie metody calculateBMI na kontrolerze
        // Jeśli masz już dostęp do kontrolera, to wywołaj bezpośrednio jego metodę calculateBMI
         double bmi = controller.calculateBMI(weight, height);

        // Teraz użyj TestFX, aby sprawdzić, czy etykieta bmiStatusLabel wyświetla poprawny tekst i jest widoczna
        // replace `controller.bmiStatusLabel` z rzeczywistą referencją do kontrolera i jego etykiety, jeśli jest dostępna
        verifyThat("#bmiStatusLabel", isVisible()); // Upewniamy się, że etykieta jest widoczna
        verifyThat("#bmiStatusLabel", hasText("16.33")); // Sprawdzamy, czy etykieta ma odpowiedni tekst (w tym przypadku oczekiwane BMI)
    }

    // Podobnie możesz dodać więcej testów dla innych przypadków, np. normalnej wagi i wzrostu, nadwagi, otyłości itp.
}
