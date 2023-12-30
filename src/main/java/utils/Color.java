package utils;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class Color {
    /**
     * ANSI escape code for resetting the color
     */
    public static final String ANSI_RESET = "\u001B[0m";
    /**
     * ANSI escape codes for coloring the text
     */
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * ANSI escape codes for coloring the background of the text
     */
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    /**
     * The ColorBgString method colors the background of a string and the text of the string.
     * It takes a background color, a string, and a text color as parameters.
     * It returns the string with the colored background and text.
     * @param background the ANSI escape code for the background color
     * @param string the string to color
     * @param color the ANSI escape code for the text color
     * @return the string with the colored background and text
     */
    public static String ColorBgString(String background, String string, String color) {
        return background + color + string + ANSI_RESET;
    }

    /**
     * The ColorString method colors the text of a string.
     * It takes a string and a color as parameters.
     * It returns the string with the colored text.
     * @param string the string to color
     * @param color the ANSI escape code for the text color
     * @return the string with the colored text
     */
    public static String ColorString(String string, String color) {
        return color + string + ANSI_RESET;
    }

    public static RadialGradient greenGradient() {
        return new RadialGradient(
                0.5165745856353591, 0.5, 0.6264367816091954, 0, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, javafx.scene.paint.Color.color(0, 0.9868420958518982, 0.47697368264198303)),
                new Stop(1, javafx.scene.paint.Color.WHITE)
        );
    }

    public static RadialGradient redGradient() {
        return new RadialGradient(
                0.5165745856353591, 0.5, 0.6264367816091954, 0, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, javafx.scene.paint.Color.RED),
                new Stop(1, javafx.scene.paint.Color.WHITE)
        );
    }
}