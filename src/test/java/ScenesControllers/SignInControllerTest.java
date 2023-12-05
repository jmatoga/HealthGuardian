package ScenesControllers;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.api.FxRobot;
import static org.testfx.util.NodeQueryUtils.*;


import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Color;

import java.io.IOException;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@ExtendWith(ApplicationExtension.class)
public class SignInControllerTest {
    private SignInController controller = new SignInController();
    private boolean testSuccess = false;

    @Start
    private void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/healthguardian/SignInScene.fxml"));
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
    public void testCheckWrittenText_RightValues(FxRobot robot) throws IOException {
        //controller = mock(SignInController.class);
        robot.clickOn("#firstName").write("John");
        robot.clickOn("#lastName").write("Doe");
        robot.clickOn("#email").write("johndoe@example.com");
        robot.clickOn("#phoneNumber").write("123456789");
        robot.clickOn("#pesel").write("12345678901");
        robot.clickOn("#username").write("johndoe");
        robot.clickOn("#password").write("password");
        //robot.clickOn("#signInButton");
        //doNothing().when(controller).signInUser();

        controller.setFirstName(robot.lookup("#firstName").query());
        controller.setLastName(robot.lookup("#lastName").query());
        controller.setEmail(robot.lookup("#email").query());
        controller.setPhoneNumber(robot.lookup("#phoneNumber").query());
        controller.setPesel(robot.lookup("#pesel").query());
        controller.setUsername(robot.lookup("#username").query());
        controller.setPassword(robot.lookup("#password").query());
        //assertEquals("John", textField.getText());

        //System.out.println(controller.getFirstName().getText());
        assertTrue(controller.checkWrittenText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_AllFieldsEmpty1(FxRobot robot) {
        robot.clickOn("#signInButton");

        //robot.clickOn(".button");

        assertFalse(controller.checkWrittenText());
        assertThat(controller.getSignInStatus(), LabeledMatchers.hasText("You have to fill all gaps!"));
        assertEquals("You have to fill all gaps!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_ValidInput1() {
        controller.setFirstName("John");
        controller.setLastName("Doe");
        controller.setEmail("johndoe@example.com");
        controller.setPhoneNumber("123456789");
        controller.setPesel("12345678901");
        controller.setUsername("johndoe");
        controller.setPassword("password");

        assertTrue(controller.checkWrittenText());
        assertEquals("", controller.getSignInStatus().getText());

        //assertThat(controller.getSignInStatus(), LabeledMatchers.hasText(""));
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidFirstName() {
        setLabelsWithRightText();
        controller.setFirstName("John Doe");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong first name!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidLastName() {
        setLabelsWithRightText();
        controller.setLastName("John Doe");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong last name!", controller.getSignInStatus().getText());
        controller.setLastName("John11Doe");
        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong last name!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidEmail() {
        setLabelsWithRightText();
        controller.setEmail("john.doe@example");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong email!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidPhoneNumber() {
        setLabelsWithRightText();
        controller.setPhoneNumber("1234567");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong phone number!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidPesel() {
        setLabelsWithRightText();
        controller.setPesel("1234567890");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong pesel!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidUsername() {
        setLabelsWithRightText();
        controller.setUsername("john doe");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong username, space inside!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @Test
    public void testCheckWrittenText_InvalidPassword() {
        setLabelsWithRightText();
        controller.setPassword("password with space");

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong password!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

//    @Test
//    public void testCheckWrittenText_ValidInput() {
//        SignInController controller = new SignInController();
//        controller.firstName.setText("John");
//        controller.lastName.setText("Doe");
//        controller.email.setText("john.doe@example.com");
//        controller.phoneNumber.setText("123456789");
//        controller.pesel.setText("12345678901");
//        controller.username.setText("johndoe");
//        controller.password.setText("password");
//
//        assertTrue(controller.checkWrittenText());
//        assertEquals("", controller.signInStatus.getText());
//    }

    private void setLabelsWithRightText() {
        controller.setFirstName("John");
        controller.setLastName("Doe");
        controller.setEmail("johndoe@example.com");
        controller.setPhoneNumber("123456789");
        controller.setPesel("12345678901");
        controller.setUsername("johndoe");
        controller.setPassword("password");
    }

    @AfterEach
    public void testResult(TestInfo testInfo) {
        if (testSuccess)
            System.out.println(Color.ColorString(testInfo.getDisplayName() + " executed successfully!", Color.ANSI_GREEN));
        else
            System.out.println(Color.ColorString(testInfo.getDisplayName() + " failed!", Color.ANSI_RED));
    }

    @AfterAll
    static void afterAll() {
        System.out.println(Color.ColorString("All tests have been conducted.", Color.ANSI_BLACK_BACKGROUND));
    }
}
