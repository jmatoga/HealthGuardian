package utils;

import java.io.PrintWriter;

/**
 * The Message class provides methods for sending different types of messages to the server.
 * Each method takes a PrintWriter and a message as parameters.
 * The PrintWriter is used to send the message to the server.
 * The message is a string that contains the content of the message.
 */
public class Message {
    /**
     * Sends an input message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendInputMessage(PrintWriter writer, String message) {
        writer.println("SEND:" + message);
    }

    /**
     * Sends an exit message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendExitMessage(PrintWriter writer, String message) {
        writer.println("EXIT:" + message);
    }

    /**
     * Sends a print message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendPrintMessage(PrintWriter writer, String message) {
        writer.println("PRINT:" + message);
    }

    /**
     * Sends a login message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendLoginMessage(PrintWriter writer, String message) {
        writer.println("LOGIN:" + message);
    }
    public void sendAdminLoginMessage(PrintWriter writer, String message) {
        writer.println("ADMIN_LOGIN:" + message);
    }

    /**
     * Sends a doctor login message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendDoctorLoginMessage(PrintWriter writer, String message) {
        writer.println("DOCTOR_LOGIN:" + message);
    }

    /**
     * Sends a get name message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetNameMessage(PrintWriter writer, String message) { writer.println("GET_USER_DATA:" + message);}

    /**
     * Sends a check if user exists message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void checkIfUserExists(PrintWriter writer, String message) { writer.println("CHECK_IF_USER_EXISTS:" + message); }
    public void checkIfDoctorExists(PrintWriter writer, String message) { writer.println("CHECK_IF_DOCTOR_EXISTS:" + message); }

    /**
     * Sends a check one time code message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void checkOneTimeCode(PrintWriter writer, String message) {
        writer.println("CHECK_ONE_TIME_CODE:" + message);
    }
    public void sendCreateNewOneTimeCodesMessage(PrintWriter writer, String message) {
        writer.println("CREATE_NEW_ONE_TIME_CODES:" + message);
    }

    /**
     * Sends an update user basic data message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void updateUserBasicData(PrintWriter writer, String message) {
        writer.println("UPDATE_USER_BASIC_DATA:" + message);
    }

    /**
     * Sends a get settings message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetSettingsMessage(PrintWriter writer, String message) {
        writer.println("GET_SETTINGS:" + message);
    }
    public void sendGetDoctorSettingsMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_SETTINGS:" + message);
    }
    public void sendGetDoctorDataMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_DATA:" + message);
    }

    public void sendGetPatientMessage(PrintWriter writer, String message) {
        writer.println("GET_PATIENT:" + message);
    }

    public void prescribeEPrescription(PrintWriter writer, String message) {
        writer.println("PRESCRIBE_EPRESCRIPTION:" + message);
    }
    public void prescribeEReferral(PrintWriter writer, String message) {
        writer.println("PRESCRIBE_EREFERRAL:" + message);
    }
    public void addRecommendation(PrintWriter writer, String message) {
        writer.println("ADD_RECOMMENDATION:" + message);
    }
    public void addMedicalHistory(PrintWriter writer, String message) {
        writer.println("ADD_MEDICAL_HISTORY:" + message);
    }
    public void addNewDoctorMessage(PrintWriter writer, String message) {
        writer.println("ADD_NEW_DOCTOR:" + message);
    }

    public void generateReport(PrintWriter writer, String message) {
        writer.println("GENERATE_REPORT:" + message);
    }

    public void addDocumentation(PrintWriter writer, String message) {
        writer.println("ADD_DOCUMENTATION:" + message);
    }

    public void sendDeleteDocumentation(PrintWriter writer, String message) {
        writer.println("DELETE_DOCUMENTATION:" + message);
    }
    public void sendDeleteMedicialHistory(PrintWriter writer, String message) {
        writer.println("DELETE_MEDICAL_HISTORY:" + message);
    }

    /**
     * Sends a set settings message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendSetSettingsMessage(PrintWriter writer, String message) {
        writer.println("SET_SETTINGS:" + message);
    }

    public void sendDoctorSetSettingsMessage(PrintWriter writer, String message) {
        writer.println("SET_DOCTOR_SETTINGS:" + message);
    }

    /**
     * Sends a get clinics message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetClinicsMessage(PrintWriter writer, String message) {
        writer.println("GET_CLINICS:" + message);
    }

    /**
     * Sends a get messages message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetNotificationsMessage(PrintWriter writer, String message) {
        writer.println("GET_NOTIFICATIONS:" + message);
    }
    public void sendGetDoctorNotificationsMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_NOTIFICATIONS:" + message);
    }

    /**
     * Sends a get examinations message to the server.
     * @param writer the PrintWriter to send the message
     * @param message the content of the message
     */
    public void sendGetExaminationsMessage(PrintWriter writer, String message) {
        writer.println("GET_EXAMINATIONS:" + message);
    }
    public void sendGetPressureMessage(PrintWriter writer, String message) {
        writer.println("GET_PRESSURE:" + message);
    }
    public void sendGetMedicalHistoryMessage(PrintWriter writer, String message) {
        writer.println("GET_MEDICAL_HISTORY:" + message);
    }
    public void sendGetDoctorMedicalHistoryMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_MEDICAL_HISTORY:" + message);
    }
    public void sendGetDoctorDocumentationsMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_DOCUMENTATIONS:" + message);
    }
    public void sendGetEReferralMessage(PrintWriter writer, String message) {
        writer.println("GET_EREFERRAL:" + message);
    }
    public void sendGetEPrescriptionMessage(PrintWriter writer, String message) {
        writer.println("GET_EPRESCRIPTION:" + message);
    }
    public void sendGetFindingsMessage(PrintWriter writer, String message) {
        writer.println("GET_FINDINGS:" + message);
    }
    public void sendGetDoctorFindingsMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_FINDINGS:" + message);
    }
    public void sendGetRecommendationMessage(PrintWriter writer, String message) {
        writer.println("GET_RECOMMENDATION:" + message);
    }
    public void sendGetSMIMessage(PrintWriter writer, String message) {
        writer.println("GET_SMI:" + message);
    }
    public void addShortMedicalInterview(PrintWriter writer, String message) {
        writer.println("ADD_SMI:" + message);
    }
    public void addReferralSMI(PrintWriter writer, String message) {
        writer.println("ADD_SMI_EREFERRAL:" + message);
    }
    public void sendGetEContact(PrintWriter writer, String message) {
        writer.println("GET_ECONTACT:" + message);
    }
    public void sendGetExaminationsForTodayMessage(PrintWriter writer, String message) {
        writer.println("GET_EXAMINATIONS_FOR_TODAY:" + message);
    }
    public void addLinkToExamination(PrintWriter writer, String message) {
        writer.println("ADD_LINK:" + message);
    }
    public void sendGetExaminationsDoctorMessage(PrintWriter writer, String message) {
        writer.println("GET_DOCTOR_EXAMINATIONS:" + message);
    }
}