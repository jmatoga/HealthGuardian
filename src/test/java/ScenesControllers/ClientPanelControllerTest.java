package ScenesControllers;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
//import static org.testfx.matcher.control.LabeledMatchers.isVisible;

public class ClientPanelControllerTest {
    private ClientPanelController controller;

    @BeforeEach
    public void setUp() {
        controller = Mockito.spy(new ClientPanelController());
        //controller.bmiStatusLabel = new Label(); // Inicjalizacja pola bmiStatusLabel
        //controller.bmiLabel = new Label();
        //controller.bmiStatusLabel = new Label();
        //controller = new ClientPanelController();
    }

    @Test
    public void testGetUserDataFromDB_CalculatesBMI() {
//        double weight = 70.0;
//        double height = 175;
//
//        doNothing().when(controller).initializeHoverEffect(); // Ignoring the call method because it is irrelevant to this test
//
//        // Act
//        controller.calculateBMI(weight, height);
//
//        double expectedBMI = 22.86;
//        double actualBMI = controller.calculateBMI(weight, height);
//
        
        double[][] testCases = {
                {40.0, 140.0, 16.33},   // niska waga, niski wzrost
                {70.0, 175.0, 22.86},   // normalna waga, normalny wzrost
                {90.0, 180.0, 27.78},   // nadwaga, normalny wzrost
                {120.0, 185.0, 35.07}  // otyłość, normalny wzrost
        };

        for (double[] testCase : testCases) {
            double weight = testCase[0];
            double height = testCase[1];
            double expectedBMI = testCase[2];

            double actualBMI = controller.calculateBMI(weight, height);
            assertEquals(expectedBMI, actualBMI, 0.01); // Verify that the BMI calculation is closest to the expected value with a tolerance of 0.01
        }
    }
}
