package ScenesControllers;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testfx.framework.junit5.ApplicationExtension;
import utils.Color;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
public class SignInControllerIntegrationTest {
    private SignInController controller = new SignInController();
    private boolean testSuccess = false;

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
    @DisplayName("Test for Valid Inputs")
    public void testCheckWrittenText_ValidInputs() {
        controller.setFirstName("John");
        controller.setLastName("Doe");
        controller.setEmail("johndoe@example.com");
        controller.setPhoneNumber("123456789");
        controller.setPesel("12345678901");
        controller.setUsername("johndoe");
        controller.setPassword("password");

        assertTrue(controller.checkWrittenText());
        assertEquals("", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid First Name")
    @ParameterizedTest(name = "Test {index} for Invalid First Name: \"{0}\"")
    @ValueSource(strings = {"John Doe", "John11Doe", "John Doe1", "John!Doe", " "})
    public void testCheckWrittenText_InvalidFirstName(String firstName) {
        setLabelsWithRightText();
        controller.setFirstName(firstName);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong first name!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid Last Name")
    @ParameterizedTest(name = "Test {index} for Invalid Last Name: \"{0}\"")
    @ValueSource(strings = {"John Doe", "John11Doe", "John Doe1", "John!Doe", " "})
    public void testCheckWrittenText_InvalidLastName(String lastName) {
        setLabelsWithRightText();
        controller.setLastName(lastName);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong last name!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid Email")
    @ParameterizedTest(name = "Test {index} for Invalid Email: \"{0}\"")
    @ValueSource(strings = {"john.doe@example", "john.doeexample.com", "johndoe@examplecom", "johnd oe@example.com", "john@doe@example.com", " ", "jo", "aaaa@a.c"})
    public void testCheckWrittenText_InvalidEmail(String email) {
        setLabelsWithRightText();
        controller.setEmail(email);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong email!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid Phone Number")
    @ParameterizedTest(name = "Test {index} for Invalid Phone Number: \"{0}\"")
    @ValueSource(strings = {"1234567", "1234567890", "12345678a", "!12345678", " "})
    public void testCheckWrittenText_InvalidPhoneNumber(String phoneNumber) {
        setLabelsWithRightText();
        controller.setPhoneNumber(phoneNumber);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong phone number!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid Pesel")
    @ParameterizedTest(name = "Test {index} for Invalid Pesel: \"{0}\"")
    @ValueSource(strings = {"1234567890", "123456789012", "1234567890a", "!12345678901", " "})
    public void testCheckWrittenText_InvalidPesel(String pesel) {
        setLabelsWithRightText();
        controller.setPesel(pesel);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong pesel!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid Username")
    @ParameterizedTest(name = "Test {index} for Invalid Username: \"{0}\"")
    @ValueSource(strings = {"john doe", " "})
    public void testCheckWrittenText_InvalidUsername(String username) {
        setLabelsWithRightText();
        controller.setUsername(username);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong username, space inside!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

    @DisplayName("Test for Invalid Password")
    @ParameterizedTest(name = "Test {index} for Invalid Password: \"{0}\"")
    @ValueSource(strings = {"password with space", " "})
    public void testCheckWrittenText_InvalidPassword(String password) {
        setLabelsWithRightText();
        controller.setPassword(password);

        assertFalse(controller.checkWrittenText());
        assertEquals("Wrong password!", controller.getSignInStatus().getText());
        testSuccess = true;
    }

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
            System.out.println(Color.ColorString(testInfo.getDisplayName() + " passed successfully!", Color.ANSI_GREEN));
        else
            System.out.println(Color.ColorString(testInfo.getDisplayName() + " failed!", Color.ANSI_RED));
    }

    @AfterAll
    static void afterAll() {
        System.out.println(Color.ColorString("All tests have been conducted.", Color.ANSI_BLACK_BACKGROUND));
    }
}
