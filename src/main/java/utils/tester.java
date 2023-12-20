package utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class tester {

    /**
     * Fyction to generate random barcodes
     * @param args
     */
//    public static void main(String[] args) {
//        int numberOfStrings = 100;
//        int stringLength = 44;
//
//        for (int i = 0; i < numberOfStrings; i++) {
//            String randomString = generateRandomString(stringLength);
//            System.out.println(randomString);
//        }
//
//    private static String generateRandomString(int length) {
//        String characters = "0123456789";
//        StringBuilder randomString = new StringBuilder(length);
//
//        SecureRandom random = new SecureRandom();
//        for (int i = 0; i < length; i++) {
//            int randomIndex = random.nextInt(characters.length());
//            randomString.append(characters.charAt(randomIndex));
//        }
//
//        return randomString.toString();
//    }

    /**
     * Fyction to generate random code
     * @param args
     */
//    public static void main (String[] args) {
//        int numberOfString = 100;
//        int stringLength = 4;
//
//        for (int i = 0; i < numberOfString; i++) {
//            String randomString = generateRandomString(stringLength);
//            System.out.println(randomString);
//        }
//
//    }
//
//    private static String generateRandomString(int length) {
//        String characters = "0123456789";
//        StringBuilder randomString = new StringBuilder(length);
//
//        SecureRandom random = new SecureRandom();
//        for(int i = 0; i < length; i ++){
//            int randomIndex = random.nextInt(characters.length());
//            randomString.append(characters.charAt(randomIndex));
//        }
//        return randomString.toString();
//    }

    /**
     * Fuction to generate dates
     * @param args
     */
    public static void main(String[] args) {
        int numberOfDates = 30;
        String startDate = "2023-01-01";
        String endDate = "2024-02-10";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        Random random = new Random();
        for (int i = 0; i < numberOfDates; i++) {
            LocalDate randomDate = start.plusDays(random.nextInt((int) (end.toEpochDay() - start.toEpochDay() + 1)));
            System.out.println(formatter.format(randomDate));
        }
    }


}
