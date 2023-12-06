package ScenesControllers;

import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.TestWatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import utils.Color;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.assertContext;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
class ClientPanelControllerTest {

    private final ClientPanelController controller = new ClientPanelController();
    private boolean testSuccess = false;

    @BeforeEach
    void setUp() {
        controller.setBmiStatusLabel(new Label());
        controller.setBmiLabel(new Label());
    }

    @Test
    @DisplayName("Test for calculateBMI method when BMI is under 18.5")
    void testCalculateBMI_Underweight() {
        verifyThat(controller.getBmiStatusLabel(), isVisible());
        verifyThat(controller.getBmiLabel(), isVisible());

        controller.calculateBMI(50.0, 170.0);
        assertEquals(Paint.valueOf("#f54040"), controller.getBmiStatusLabel().getTextFill()); // Expected color red for underweight
        assertEquals("17.30", controller.getBmiStatusLabel().getText());

        testSuccess = true;
    }

    @Test
    @DisplayName("Test for calculateBMI method when BMI is between 18.5 and 24.9")
    void testCalculateBMI_CorrectWeight() {
        verifyThat(controller.getBmiStatusLabel(), isVisible());
        verifyThat(controller.getBmiLabel(), isVisible());

        controller.calculateBMI(65.0, 175.0);
        assertEquals(Paint.valueOf("#40f546"), controller.getBmiStatusLabel().getTextFill()); // Expected green color for correct weight
        assertEquals("21.22", controller.getBmiStatusLabel().getText());

        testSuccess = true;
    }

    @Test
    @DisplayName("Test for calculateBMI method when BMI is between 25.0 and 29.99")
    void testCalculateBMI_Overweight() {
        verifyThat(controller.getBmiStatusLabel(), isVisible());
        verifyThat(controller.getBmiLabel(), isVisible());

        controller.calculateBMI(90.0, 180.0);
        assertEquals(Paint.valueOf("#f5bb40"), controller.getBmiStatusLabel().getTextFill()); // Expected yellow for overweight
        assertEquals("27.78", controller.getBmiStatusLabel().getText());

        testSuccess = true;
    }

    @Test
    @DisplayName("Test for calculateBMI method when BMI is above 30.0")
    void testCalculateBMI_Obesity() {
        verifyThat(controller.getBmiStatusLabel(), isVisible());
        verifyThat(controller.getBmiLabel(), isVisible());

        controller.calculateBMI(100.0, 165.0);
        assertEquals(Paint.valueOf("#f54040"), controller.getBmiStatusLabel().getTextFill()); // Expected color red for obesity
        assertEquals("36.73", controller.getBmiStatusLabel().getText());

        testSuccess = true;
    }

    @AfterEach
    public void testResult(TestInfo testInfo) {
        if (testSuccess)
            System.out.println(Color.ColorString(testInfo.getDisplayName() + " passed successfully!", Color.ANSI_GREEN));
        else
            System.out.println(Color.ColorString(testInfo.getDisplayName() + " failed!", Color.ANSI_RED));
    }

    @AfterAll
    static void afterAll() {
        System.out.println(Color.ColorString("All tests have been conducted.", Color.ANSI_BLACK_BACKGROUND));
    }
}
