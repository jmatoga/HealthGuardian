package ScenesControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import utils.Color;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SignInControllerEndToEndTest {
    private SignInController controller = new SignInController();
    private boolean testSuccess = false;

    @Start
    private void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ScenesLayout/SignInScene.fxml"));
        stage.setScene(new Scene(root, 800, 500));
        stage.show();
    }

    @BeforeEach
    void setUp() {
        controller.setFirstName(new TextField());
        controller.setLastName(new TextField());
        controller.setEmail(new TextField());
        controller.setPhoneNumber(new TextField());
        controller.setPesel(new TextField());
        controller.setUsername(new TextField());
        controller.setPassword(new TextField());
        controller.setSignInStatus(new Label());
        controller.setSignInButton(new Button());
    }

    @Test
    @DisplayName("Test for Valid Inputs in GUI (robot)")
    public void testCheckWrittenText_ValidInputsGUI(FxRobot robot) throws IOException {
        robot.clickOn("#firstName").write("John");
        robot.clickOn("#lastName").write("Doe");
        robot.clickOn("#email").write("johndoe@example.com");
        robot.clickOn("#phoneNumber").write("123456789");
        robot.clickOn("#pesel").write("12345678901");
        robot.clickOn("#username").write("johndoe");
        robot.clickOn("#password").write("password");

        controller.setFirstName(robot.lookup("#firstName").query());
        controller.setLastName(robot.lookup("#lastName").query());
        controller.setEmail(robot.lookup("#email").query());
        controller.setPhoneNumber(robot.lookup("#phoneNumber").query());
        controller.setPesel(robot.lookup("#pesel").query());
        controller.setUsername(robot.lookup("#username").query());
        controller.setPassword(robot.lookup("#password").query());

        assertTrue(controller.checkWrittenText());
        assertEquals("", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    @DisplayName("Test for Empty Values in GUI (robot)")
    public void testCheckWrittenText_AllFieldsEmpty(FxRobot robot) {
        robot.clickOn("#signInButton");
        //robot.clickOn(".button");

        assertFalse(controller.checkWrittenText());
        assertEquals("You have to fill all gaps!", controller.getSignInStatus().getText());
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
