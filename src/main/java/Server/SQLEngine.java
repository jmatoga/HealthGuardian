package Server;

import utils.Color;

import java.math.BigInteger;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

/**
 * The SQLEngine class is responsible for handling all database operations.
 * It provides methods for connecting to the database, executing SQL queries, and closing the database connection.
 */
public class SQLEngine {
    private final String url;
    private final String DBusername;
    private final String DBpassword;

    /**
     * Constructor for the SQLEngine class.
     *
     * @param host       the host of the database
     * @param port       the port of the database
     * @param database   the name of the database
     * @param DBusername the username for the database
     * @param DBpassword the password for the database
     */
    public SQLEngine(String host, int port, String database, String DBusername, String DBpassword) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.DBusername = DBusername;
        this.DBpassword = DBpassword;
    }

    /**
     * The connectToDataBase method is responsible for establishing a connection with the database.
     *
     * @param connection the Connection object
     * @param clientId   the ID of the client
     * @return the Connection object
     */
    public Connection connectToDataBase(Connection connection, int clientId) {
        try {
            // EMERGENCY !!!
            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
            connection = DriverManager.getConnection(url, DBusername, DBpassword);
            System.out.println(Color.ColorString("Database connection successful. (For ClientID: ", Color.ANSI_GREEN_BACKGROUND) + Color.ColorBgString(Color.ANSI_GREEN_BACKGROUND, "" + clientId, Color.ANSI_BLACK) + Color.ColorString(")", Color.ANSI_GREEN_BACKGROUND));
        } catch (SQLException e) {
            if (e.getSQLState().equals("08S01")) {
                System.out.println(Color.ColorString("ERROR! No internet connection or wrong password to database!", Color.ANSI_RED));
                System.exit(-1);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println(Color.ColorString(e.getMessage(), Color.ANSI_RED));
            e.printStackTrace();
            System.exit(-2);
        }
        return connection;
    }

    /**
     * The getData method is responsible for retrieving data from the database.
     *
     * @param clientID the ID of the client
     * @param user_id  the ID of the user
     * @return an array of strings containing the data
     */
    String[] getData(int clientID, String user_id) {
        String[] returnStatement = {"Error", "Error", "No data", "No data", "No data", "No data", "No data", "No data", "No data"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT first_name, last_name, birth_date, user_basic_data_table.weight, user_basic_data_table.height, user_basic_data_table.temperature, user_basic_data_table.systolic_pressure, user_basic_data_table.diastolic_pressure, user_basic_data_table.entry_date FROM user_table LEFT JOIN user_basic_data_table on user_table.user_id = user_basic_data_table.user_id AND (SELECT MAX(entry_date) FROM user_basic_data_table WHERE user_id = user_table.user_id) WHERE user_table.user_id = ? ORDER BY entry_date DESC LIMIT 1";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user_id);

            resultSet = preparedStatement.executeQuery();

            boolean ifResultSetHasNext = resultSet.next();

            returnStatement[0] = String.valueOf(resultSet.getString("first_name"));
            returnStatement[1] = String.valueOf(resultSet.getString("last_name"));

            if (ifResultSetHasNext && resultSet.getDate("birth_date") != null) {
                System.out.println("User Data getted correctly.");

                returnStatement[2] = String.valueOf(resultSet.getDate("birth_date"));
                returnStatement[3] = resultSet.getString("user_basic_data_table.weight");
                returnStatement[4] = resultSet.getString("user_basic_data_table.height");
                returnStatement[5] = resultSet.getString("user_basic_data_table.temperature");
                returnStatement[6] = resultSet.getString("user_basic_data_table.systolic_pressure");
                returnStatement[7] = resultSet.getString("user_basic_data_table.diastolic_pressure");
                returnStatement[8] = String.valueOf(resultSet.getDate("user_basic_data_table.entry_date"));

            } else if (ifResultSetHasNext && resultSet.getDate("birth_date") == null) {
                System.out.println("First login! Insert data.");
            } else {
                System.out.println("Error in database while getting user data.");

                returnStatement[2] = "Error";
                returnStatement[3] = "Error";
                returnStatement[4] = "Error";
                returnStatement[5] = "Error";
                returnStatement[6] = "Error";
                returnStatement[7] = "Error";
                returnStatement[8] = "Error";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Generates a report containing various statistics and information based on the specified client ID.
     *
     * @param clientID the unique identifier of the client for whom the report is generated
     * @return an ArrayList of Strings containing the report information, or null if an error occurs
     */
    ArrayList<String> generateReport(int clientID) {
        ArrayList<String> returnStatement = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT COUNT(*) AS 'users' " +
                                 "FROM user_table " +
                                 "UNION " +
                                 "SELECT COUNT(*) AS 'doctors' " +
                                 "FROM doctor_table; ";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            int columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting count of users and doctors.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    if (row == 0)
                        returnStatement.add("Users: " + resultSet.getString(col + 1));
                    else
                        returnStatement.add("Doctors: " + resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }
            returnStatement.add("&/&");

            sql = "SELECT c.clinic_id, " +
                          "c.clinic_nr, " +
                          "c.name AS clinic_name, " +
                          "c.adress, " +
                          "c.phone, " +
                          "CONCAT(d.first_name, ' ', d.last_name) AS clinic_director " +
                          "FROM " +
                          "clinic_table c " +
                          "LEFT JOIN " +
                          "doctor_table d ON c.director_id = d.doctor_id; ";

            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting list of clinics.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    if (col == 0)
                        returnStatement.add("ID: " + resultSet.getString(col + 1));
                    else if (col == 1)
                        returnStatement.add("No: " + resultSet.getString(col + 1));
                    else if (col == 2)
                        returnStatement.add("Name: " + resultSet.getString(col + 1));
                    else if (col == 3)
                        returnStatement.add("Adress: " + resultSet.getString(col + 1));
                    else if (col == 4)
                        returnStatement.add("Phone: " + resultSet.getString(col + 1));
                    else
                        returnStatement.add("Director: " + resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }
            returnStatement.add("&/&");

            sql = "SELECT DATE_FORMAT(date_of_issue, '%Y-%m') AS issued_month, " +
                          "COUNT(referral_id) AS prescriptions_issued " +
                          "FROM " +
                          "e_referral_table " +
                          "WHERE " +
                          "date_of_issue >= '2022-01-01' AND date_of_issue <= CURDATE() " +
                          "GROUP BY " +
                          "issued_month " +
                          "ORDER BY issued_month; ";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting count of prescribed e-referral in each month.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement.add(resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }
            returnStatement.add("&/&");

            sql = "SELECT DATE_FORMAT(date_of_issue, '%Y-%m') AS issued_month, " +
                          "COUNT(prescription_id) AS prescriptions_issued " +
                          "FROM " +
                          "e_prescription_table " +
                          "WHERE " +
                          "date_of_issue >= '2022-01-01' AND date_of_issue <= CURDATE() " +
                          "GROUP BY " +
                          "issued_month " +
                          "ORDER BY issued_month; ";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting count of prescribed e-prescription in each month.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement.add(resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }
            returnStatement.add("&/&");

            sql = "SELECT clinic_id, COUNT(user_id) AS number_of_users " +
                          "FROM user_clinic_table " +
                          "GROUP BY clinic_id; ";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting number of users in each clinic.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    if (col == 0)
                        returnStatement.add("ID: " + resultSet.getString(col + 1));
                    else
                        returnStatement.add("Users: " + resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }
            returnStatement.add("&/&");

            sql = "SELECT clinic_id, COUNT(doctor_id) AS number_of_doctors " +
                          "FROM doctor_clinic_table " +
                          "GROUP BY clinic_id; ";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting number of doctors in each clinic.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    if (col == 0)
                        returnStatement.add("ID: " + resultSet.getString(col + 1));
                    else
                        returnStatement.add("Doctors: " + resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }
            returnStatement.add("&/&");

            sql = "SELECT * FROM one_time_code_table";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = preparedStatement.executeQuery();

            rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            columnCount = resultSet.getMetaData().getColumnCount();

            if (rowCount <= 0) {
                System.out.println("Error in database while getting one time codes.");
                returnStatement.add("Error");
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement.add(resultSet.getString(col + 1));
                }
                row++;
                returnStatement.add("$/$");
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves data for a specific doctor based on the provided doctor ID.
     *
     * @param clientID   the unique identifier of the client for whom the data is retrieved
     * @param doctor_id  the ID of the doctor for whom the data is requested
     * @return an array of Strings containing the doctor's first name, last name, and profession,
     *         or an array of "Error" if an error occurs during the database operation
     */
    String[] getDoctorData(int clientID, String doctor_id) {
        String[] returnStatement = {"Error", "Error", "Error"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT first_name, last_name, profession FROM doctor_table WHERE doctor_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, doctor_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                returnStatement[0] = resultSet.getString("first_name");
                returnStatement[1] = resultSet.getString("last_name");
                returnStatement[2] = resultSet.getString("profession");
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Retrieves data for a specific patient based on the provided PESEL.
     *
     * @param clientID the unique identifier of the client for whom the data is retrieved
     * @param pesel    the PESEL of the patient for whom the data is requested
     * @return an array of Strings containing the patient's first name, last name, and birth date,
     *         or an array of "Error" if an error occurs during the database operation
     */
    String[] getPatient(int clientID, String pesel) {
        String[] returnStatement = {"Error", "Error", "Error"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT first_name, last_name, birth_date FROM user_table WHERE pesel = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, pesel);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                returnStatement[0] = resultSet.getString("first_name");
                returnStatement[1] = resultSet.getString("last_name");
                returnStatement[2] = resultSet.getString("birth_date");
            } else {
                returnStatement[0] = "Patient not found";
                returnStatement[1] = "Patient not found";
                returnStatement[2] = "Patient not found";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
            returnStatement[0] = "Error";
            returnStatement[1] = "Error";
            returnStatement[2] = "Error";
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * The checkIfUserExists method is responsible for checking if a user exists in the database.
     *
     * @param clientID the ID of the client
     * @param username the username of the user
     * @param email    the email of the user
     * @return a string indicating if the user exists
     */
    String checkIfUserExists(int clientID, String username, String email) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet userResultSet = null;
        ResultSet emailResultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT username FROM user_pass_table WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);  // username

            userResultSet = preparedStatement.executeQuery();

            String emailSql = "SELECT email FROM user_table WHERE email = ?";
            preparedStatement = connection.prepareStatement(emailSql);
            preparedStatement.setString(1, email);  // email

            emailResultSet = preparedStatement.executeQuery();
            if (userResultSet.next()) { // if username exist in database
                System.out.println("User exists!");
                return "User exists";
            } else if (emailResultSet.next()) { // if email exist in database
                System.out.println("Email exists!");
                return "Email exists";
            } else {
                System.out.println("Correct, username and email free to use.");
                return "Free to use";
            }

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(userResultSet, emailResultSet, preparedStatement, connection);
        }
        return "Error";
    }

    /**
     * Checks if a doctor with the specified username, email, and clinic exists in the database.
     *
     * @param clientID the unique identifier of the client for whom the check is performed
     * @param username the username to check
     * @param email    the email to check
     * @param clinic   the clinic name to check
     * @return a string indicating the result of the check:
     *         - "Doctor exists" if the username already exists in the database
     *         - "Email exists" if the email already exists in the database
     *         - "Wrong clinic name" if the clinic name is not found in the database
     *         - "Free to use, clinic ID: {clinic_id}" if username, email, and clinic are free to use
     *         - "Error" if an error occurs during the database operation
     * @throws SQLException if a SQL exception occurs during the operation
     */
    String checkIfDoctorExists(int clientID, String username, String email, String clinic) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet userResultSet = null;
        ResultSet emailResultSet = null;
        ResultSet clinicResultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT username FROM doctor_pass_table WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);  // username

            userResultSet = preparedStatement.executeQuery();

            String emailSql = "SELECT email FROM doctor_table WHERE email = ?";
            preparedStatement = connection.prepareStatement(emailSql);
            preparedStatement.setString(1, email);  // email

            emailResultSet = preparedStatement.executeQuery();

            String clinicSql = "SELECT clinic_id FROM clinic_table WHERE name = ?";
            preparedStatement = connection.prepareStatement(clinicSql);
            preparedStatement.setString(1, clinic);  // clinic

            clinicResultSet = preparedStatement.executeQuery();

            if (userResultSet.next()) { // if username exist in database
                System.out.println("Doctor exists!");
                return "Doctor exists";
            } else if (emailResultSet.next()) { // if email exist in database
                System.out.println("Email exists!");
                return "Email exists";
            } else if (!clinicResultSet.next()) {
                System.out.println("Wrong clinic name!");
                return "Wrong clinic name";
            } else {
                System.out.println("Correct, username and email free to use, right clinic name.");
                return "Free to use, clinic ID: " + clinicResultSet.getInt("clinic_id");
            }

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            if (clinicResultSet != null)
                clinicResultSet.close();
            disconnectFromDataBase(userResultSet, emailResultSet, preparedStatement, connection);
        }
        return "Error";
    }

    /**
     * The checkOneTimeCode method is responsible for checking the one-time code of a user.
     *
     * @param clientID    the ID of the client
     * @param oneTimeCode the one-time code of the user
     * @param firstname   the first name of the user
     * @param lastname    the last name of the user
     * @param email       the email of the user
     * @param phoneNumber the phone number of the user
     * @param pesel       the PESEL number of the user
     * @param username    the username of the user
     * @param password    the password of the user
     * @return a string indicating if the one-time code is correct
     */
    String checkOneTimeCode(int clientID, String oneTimeCode, String firstname, String lastname, String email, String phoneNumber, String pesel, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "DELETE FROM one_time_code_table WHERE one_time_code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, oneTimeCode);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) { // if changed rows = 1 then code exist in database
                System.out.println("Correct code!");

                String getMaxUserIdSql = "SELECT MAX(user_id) AS max_user_id FROM user_table";
                String getSaltSql = "SET @salt = SUBSTRING(MD5(RAND()), 1, 16)";
                String insertUserSql = "INSERT INTO user_table (user_id, first_name, last_name, phone, email, pesel) VALUES (?, ?, ?, ?, ?, ?)";
                //String insertUserSqlData = "INSERT INTO user_basic_data_table (user_id) VALUES (?)";
                String insertUserPassSql = "INSERT INTO user_pass_table (username, password_hash, salt, user_id) VALUES (?, SHA2(CONCAT(?, @salt), 256), @salt, ?)";
                String insertSettingsSql = "INSERT INTO settings_table (bmi_setting, age_setting, currentDate_setting, weightInChart_setting, temperatureInChart_setting, background_setting, user_id) VALUES ('false', 'false', 'false', 'false', 'false', 'false', ?)";

                // get Max user_id from Database
                preparedStatement = connection.prepareStatement(getMaxUserIdSql);
                resultSet = preparedStatement.executeQuery();
                resultSet.next();
                int maxUserId = resultSet.getInt("max_user_id");

                // generate salt
                preparedStatement = connection.prepareStatement(getSaltSql);
                preparedStatement.executeUpdate();

                // insert into user table
                preparedStatement = connection.prepareStatement(insertUserSql);
                preparedStatement.setInt(1, maxUserId + 1);
                preparedStatement.setString(2, firstname);
                preparedStatement.setString(3, lastname);
                preparedStatement.setString(4, phoneNumber);
                preparedStatement.setString(5, email);
                preparedStatement.setString(6, pesel);
                preparedStatement.executeUpdate();

                // insert into user_basic_data_table
//                preparedStatement = connection.prepareStatement(insertUserSqlData);
//                preparedStatement.setInt(1, maxUserId + 1);
//                preparedStatement.executeUpdate();

                // insert into settings_table
                preparedStatement = connection.prepareStatement(insertSettingsSql);
                preparedStatement.setInt(1, maxUserId + 1);
                preparedStatement.executeUpdate();

                // insert into user_pass_table table
                preparedStatement = connection.prepareStatement(insertUserPassSql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setInt(3, maxUserId + 1);
                preparedStatement.executeUpdate();

                return "true";
            } else {
                System.out.println("Wrong code!");
                return "false";
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return "error";
    }

    /**
     * Adds a new doctor to the database with the provided information.
     *
     * @param clientID    the unique identifier of the client for whom the doctor is added
     * @param firstname   the first name of the doctor
     * @param lastname    the last name of the doctor
     * @param phoneNumber the phone number of the doctor
     * @param email       the email of the doctor
     * @param gender      the gender of the doctor
     * @param profession  the profession of the doctor
     * @param username    the username for the doctor's login
     * @param password    the password for the doctor's login
     * @param clinicId    the ID of the clinic to which the doctor is associated
     * @return a string indicating the result of the operation:
     *         - "Added new doctor correctly." if the doctor is added successfully
     *         - "Error while adding new doctor." if an error occurs during the operation
     */
    String addNewDoctor(int clientID, String firstname, String lastname, String phoneNumber, String email, String gender, String profession, String username, String password, String clinicId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);

            String getMaxDoctorIdSql = "SELECT MAX(doctor_id) AS max_doctor_id FROM doctor_table";
            String getSaltSql = "SET @salt = SUBSTRING(MD5(RAND()), 1, 16)";
            String insertDoctorSql = "INSERT INTO doctor_table (doctor_id, first_name, last_name, phone, email, gender, profession) VALUES (?, ?, ?, ?, ?, ?, ?)";
            String insertDoctorPassSql = "INSERT INTO doctor_pass_table (username, password_hash, salt, doctor_id) VALUES (?, SHA2(CONCAT(?, @salt), 256), @salt, ?)";
            String insertSettingsSql = "INSERT INTO doctor_settings_table (bmi_setting, age_setting, currentDate_setting, weightInChart_setting, temperatureInChart_setting, doctor_id) VALUES ('false', 'false', 'false', 'false', 'false', ?)";
            String insertDoctorClinicSql = "INSERT INTO doctor_clinic_table (doctor_id, clinic_id) VALUES (?, ?)";

            // get Max doctor_id from Database
            preparedStatement = connection.prepareStatement(getMaxDoctorIdSql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int maxDoctorId = resultSet.getInt("max_doctor_id");

            // generate salt
            preparedStatement = connection.prepareStatement(getSaltSql);
            preparedStatement.executeUpdate();

            // insert into doctor table
            preparedStatement = connection.prepareStatement(insertDoctorSql);
            preparedStatement.setInt(1, maxDoctorId + 1);
            preparedStatement.setString(2, firstname);
            preparedStatement.setString(3, lastname);
            preparedStatement.setString(4, phoneNumber);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, profession);
            int rowsAffected1 = preparedStatement.executeUpdate();

            // insert into settings_table
            preparedStatement = connection.prepareStatement(insertSettingsSql);
            preparedStatement.setInt(1, maxDoctorId + 1);
            int rowsAffected2 = preparedStatement.executeUpdate();

            // insert into doctor_pass_table table
            preparedStatement = connection.prepareStatement(insertDoctorPassSql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, maxDoctorId + 1);
            int rowsAffected3 = preparedStatement.executeUpdate();

            // insert into doctor_clinic_table
            preparedStatement = connection.prepareStatement(insertDoctorClinicSql);
            preparedStatement.setInt(1, maxDoctorId + 1);
            preparedStatement.setInt(2, Integer.parseInt(clinicId));
            int rowsAffected4 = preparedStatement.executeUpdate();

            if (rowsAffected1 == 1 && rowsAffected2 == 1 && rowsAffected3 == 1 && rowsAffected4 == 1) {
                System.out.println("Added new doctor correctly.");
                return "Added new doctor correctly.";
            } else {
                System.out.println("Error while adding new doctor. {" + rowsAffected2 + ", " + rowsAffected3 + ", " + rowsAffected4 + "}");
                return "Error while adding new doctor.";
            }

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return "error";
    }

    /**
     * The loginToAccount method is responsible for logging in a user.
     *
     * @param clientID      the ID of the client
     * @param inputUsername the input username of the user
     * @param inputPassword the input password of the user
     * @return an array of strings containing the login status and the user ID
     */
    String[] loginToAccount(int clientID, String inputUsername, String inputPassword) {
        String[] returnStatement = {"false", "-1"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT username,user_id FROM user_pass_table WHERE username = ? AND password_hash = SHA2(CONCAT(?, (SELECT salt FROM user_pass_table WHERE username=?)), 256)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inputUsername);  // username
            preparedStatement.setString(2, inputPassword);  // password
            preparedStatement.setString(3, inputUsername);  // username

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if username and password exist in database in same user
                System.out.println("Correct password.");

                returnStatement[0] = "true";
                returnStatement[1] = String.valueOf(resultSet.getInt("user_id"));

                return returnStatement;
            } else {
                System.out.println("Wrong password.");

                returnStatement[0] = "false";
                returnStatement[1] = "-1";

                return returnStatement;
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Logs in a doctor with the provided username and password.
     *
     * @param clientID      the unique identifier of the client for whom the login is performed
     * @param inputUsername the username entered by the doctor
     * @param inputPassword the password entered by the doctor
     * @return an array of Strings containing the login status and doctor ID:
     *         - {"true", "{doctor_id}"} if the login is successful
     *         - {"false", "-1"} if the login fails
     */
    String[] loginToDoctor(int clientID, String inputUsername, String inputPassword) {
        String[] returnStatement = {"false", "-1"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT username, doctor_id FROM doctor_pass_table WHERE username = ? AND password_hash = SHA2(CONCAT(?, (SELECT salt FROM doctor_pass_table WHERE username=?)), 256)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inputUsername);  // username
            preparedStatement.setString(2, inputPassword);  // password
            preparedStatement.setString(3, inputUsername);  // username

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if username and password exist in database in same user
                returnStatement[0] = "true";
                returnStatement[1] = String.valueOf(resultSet.getInt("doctor_id"));

                return returnStatement;
            } else {
                returnStatement[0] = "false";
                returnStatement[1] = "-1";

                return returnStatement;
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Logs in an admin with the provided admin ID and password.
     *
     * @param clientID      the unique identifier of the client for whom the login is performed
     * @param adminId       the admin ID entered by the admin
     * @param inputPassword the password entered by the admin
     * @return an array of Strings containing the login status and admin ID:
     *         - {"true", "{admin_id}"} if the login is successful
     *         - {"false", "-1"} if the login fails
     */
    String[] loginToAdmin(int clientID, int adminId, String inputPassword) {
        String[] returnStatement = {"false", "-1"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT admin_id FROM admin_pass_table WHERE admin_id = ? AND password_hash = SHA2(CONCAT(?, (SELECT salt FROM admin_pass_table WHERE admin_id = ?)), 256)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, adminId);  // password
            preparedStatement.setString(2, inputPassword);  // password
            preparedStatement.setInt(3, adminId);  // password

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if username and password exist in database in same user
                returnStatement[0] = "true";
                returnStatement[1] = String.valueOf(resultSet.getInt("admin_id"));

                return returnStatement;
            } else {
                returnStatement[0] = "false";
                returnStatement[1] = "-1";

                return returnStatement;
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * The updateUserBasicData method is responsible for updating the basic data of a user.
     *
     * @param clientID           the ID of the client
     * @param birthdayDate       the birthday date of the user
     * @param weight             the weight of the user
     * @param height             the height of the user
     * @param temperature        the temperature of the user
     * @param systolic_pressure  the systolic pressure of the user
     * @param diastolic_pressure the diastolic pressure of the user
     * @param entryDate          the entry date of the user
     * @param userId             the ID of the user
     * @return a string indicating the status of the update operation
     */
    String updateUserBasicData(int clientID, String birthdayDate, String weight, String height, String temperature, String systolic_pressure, String diastolic_pressure, String entryDate, String userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String howManyRowsSql = "SELECT COUNT(*) FROM user_basic_data_table WHERE user_id = ?";

            // check how many logs exist in database
            preparedStatement = connection.prepareStatement(howManyRowsSql);
            preparedStatement.setInt(1, Integer.parseInt(userId));
            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            if (resultSet.next()) {
                rowCount = resultSet.getInt(1);
            }

            if (rowCount >= 0 && rowCount <= 4) {
                String insertUserBasicDataSql = "INSERT INTO user_basic_data_table (weight, height, systolic_pressure, diastolic_pressure, temperature, entry_date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                String updateUserTableSql = "UPDATE user_table SET birth_date = ? WHERE user_id = ?";

                // insert into user basic date table
                preparedStatement = connection.prepareStatement(insertUserBasicDataSql);
                preparedStatement.setDouble(1, Double.parseDouble(weight));
                preparedStatement.setDouble(2, Double.parseDouble(height));
                preparedStatement.setInt(3, Integer.parseInt(systolic_pressure));
                preparedStatement.setInt(4, Integer.parseInt(diastolic_pressure));
                preparedStatement.setDouble(5, Double.parseDouble(temperature));
                preparedStatement.setTimestamp(6, Timestamp.valueOf(entryDate));
                preparedStatement.setInt(7, Integer.parseInt(userId));
                int rowsAffected = preparedStatement.executeUpdate();

                int rowsAffected1 = 0;
                if (!birthdayDate.equals("withoutBirthdayDate")) {
                    preparedStatement = connection.prepareStatement(updateUserTableSql);
                    preparedStatement.setDate(1, Date.valueOf(birthdayDate));
                    preparedStatement.setInt(2, Integer.parseInt(userId));
                    rowsAffected1 = preparedStatement.executeUpdate();
                } else
                    rowsAffected1 = 1;

                if (rowsAffected == 1 && rowsAffected1 == 1) {
                    System.out.println("Updated user basic data correctly.");
                    return "Updated user basic data correctly.";
                }
            } else if (rowCount == 5) {
                String updateUserBasicDataSql = "UPDATE user_basic_data_table SET weight = ?, height = ?, systolic_pressure = ?, diastolic_pressure = ?, temperature = ?, entry_date = ? WHERE user_ID = ? AND entry_date = (SELECT MIN_DATE.entry_date FROM (SELECT MIN(entry_date) as entry_date FROM user_basic_data_table WHERE user_id = ?) AS MIN_DATE)";
                preparedStatement = connection.prepareStatement(updateUserBasicDataSql);
                preparedStatement.setDouble(1, Double.parseDouble(weight));
                preparedStatement.setDouble(2, Double.parseDouble(height));
                preparedStatement.setInt(3, Integer.parseInt(systolic_pressure));
                preparedStatement.setInt(4, Integer.parseInt(diastolic_pressure));
                preparedStatement.setDouble(5, Double.parseDouble(temperature));
                preparedStatement.setTimestamp(6, Timestamp.valueOf(entryDate)); // date with time
                preparedStatement.setInt(7, Integer.parseInt(userId));
                preparedStatement.setInt(8, Integer.parseInt(userId));
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected == 1) {
                    System.out.println("Updated user basic data correctly.");
                    return "Updated user basic data correctly.";
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        System.out.println(Color.ColorString("Error while updating user basic data.", Color.ANSI_RED));
        return "Error while updating user basic data.";
    }

    /**
     * This method is responsible for closing the database connection and associated resources.
     * It closes the ResultSet, PreparedStatement, and Connection objects.
     * If any of these objects are null, they are ignored.
     *
     * @param resultSet         the ResultSet object to be closed
     * @param preparedStatement the PreparedStatement object to be closed
     * @param connection        the Connection object to be closed
     */
    private static void disconnectFromDataBase(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        // Closing resources (ResultSet, PreparedStatement, Connection)
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
            System.out.println(Color.ColorString("Database connection closed successfully.", Color.ANSI_GREEN));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while closing resources: " + e.getMessage());
        }
    }

    /**
     * This method is responsible for closing the database connection and associated resources.
     * It closes two ResultSet objects, a PreparedStatement, and a Connection object.
     * If any of these objects are null, they are ignored.
     *
     * @param resultSet         the first ResultSet object to be closed
     * @param resultSet1        the second ResultSet object to be closed
     * @param preparedStatement the PreparedStatement object to be closed
     * @param connection        the Connection object to be closed
     */
    private static void disconnectFromDataBase(ResultSet resultSet, ResultSet resultSet1, PreparedStatement preparedStatement, Connection connection) {
        // Closing resources (ResultSet, PreparedStatement, Connection)
        try {
            if (resultSet != null) resultSet.close();
            if (resultSet1 != null) resultSet1.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
            System.out.println(Color.ColorString("Database connection closed successfully.", Color.ANSI_GREEN));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while closing resources: " + e.getMessage());
        }
    }

    /**
     * The getSettings method is responsible for retrieving the settings of a user.
     *
     * @param clientID the ID of the client
     * @param user_id  the ID of the user
     * @return an array of strings containing the settings
     */
    String[] getSettings(int clientID, String user_id) {
        String[] returnStatement = {"Error", "Error", "Error", "Error", "Error", "Error"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM settings_table WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Settings getted correctly.");
                returnStatement[0] = String.valueOf(resultSet.getString("bmi_setting"));
                returnStatement[1] = String.valueOf(resultSet.getString("age_setting"));
                returnStatement[2] = String.valueOf(resultSet.getString("currentDate_setting"));
                returnStatement[3] = String.valueOf(resultSet.getString("weightInChart_setting"));
                returnStatement[4] = String.valueOf(resultSet.getString("temperatureInChart_setting"));
                returnStatement[5] = String.valueOf(resultSet.getString("background_setting"));
            } else
                System.out.println("Error in database while getting settings.");

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Retrieves the settings for a specific doctor from the database.
     *
     * @param clientID  the unique identifier of the client for whom the settings are retrieved
     * @param doctor_id the ID of the doctor for whom the settings are retrieved
     * @return an array of Strings containing the doctor's settings:
     *         - {bmi_setting, age_setting, currentDate_setting, weightInChart_setting, temperatureInChart_setting}
     *         - {"Error", "Error", "Error", "Error", "Error"} if there is an error during the retrieval
     */
    String[] getDoctorSettings(int clientID, String doctor_id) {
        String[] returnStatement = {"Error", "Error", "Error", "Error", "Error"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM doctor_settings_table WHERE doctor_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, doctor_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Settings getted correctly.");
                returnStatement[0] = String.valueOf(resultSet.getString("bmi_setting"));
                returnStatement[1] = String.valueOf(resultSet.getString("age_setting"));
                returnStatement[2] = String.valueOf(resultSet.getString("currentDate_setting"));
                returnStatement[3] = String.valueOf(resultSet.getString("weightInChart_setting"));
                returnStatement[4] = String.valueOf(resultSet.getString("temperatureInChart_setting"));
            } else
                System.out.println("Error in database while getting settings.");

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * The setSettings method is responsible for setting the settings of a user.
     *
     * @param clientID                   the ID of the client
     * @param user_id                    the ID of the user
     * @param bmi_setting                the BMI setting of the user
     * @param age_setting                the age setting of the user
     * @param currentDate_setting        the current date setting of the user
     * @param weightInChart_setting      the fourth setting of the user
     * @param temperatureInChart_setting the fifth setting of the user
     * @return a string indicating the status of the set operation
     */
    String setSettings(int clientID, String user_id, String bmi_setting, String age_setting, String currentDate_setting, String weightInChart_setting, String temperatureInChart_setting, String background_setting) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "UPDATE settings_table SET bmi_setting = ?, age_setting = ?, currentDate_setting = ?, weightInChart_setting = ?, temperatureInChart_setting = ?, background_setting = ? WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, bmi_setting);
            preparedStatement.setString(2, age_setting);
            preparedStatement.setString(3, currentDate_setting);
            preparedStatement.setString(4, weightInChart_setting);
            preparedStatement.setString(5, temperatureInChart_setting);
            preparedStatement.setString(6, background_setting);
            preparedStatement.setInt(7, Integer.parseInt(user_id));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Settings changed correctly.");
                returnStatement = "Settings changed correctly.";

            } else {
                System.out.println("Error in database while setting settings.");
                returnStatement = "Error in database while setting settings.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Prescribes an e-prescription for a patient.
     *
     * @param clientID    the unique identifier of the client for whom the e-prescription is prescribed
     * @param medicines   a string containing the prescribed medicines
     * @param date        the date when the e-prescription is issued
     * @param doctor_id   the ID of the prescribing doctor
     * @param pesel       the PESEL of the patient for whom the e-prescription is prescribed
     * @return a string indicating the status of the e-prescription prescription:
     *         - "E-prescription prescribed correctly." if the prescription is successful
     *         - "Error in database while prescribing E-prescription." if an error occurs during prescription
     *         - "Error" if there is any other error during the process
     */
    String prescribeEPrescription(int clientID, String medicines, String date, String doctor_id, String pesel) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getMaxPrescriptionIdSql = "SELECT MAX(prescription_id) AS max_prescription_id, MAX(barcode) AS max_barcode FROM e_prescription_table";
            String checkEPrescriptionCodeSql = "SELECT e_prescription_code FROM e_prescription_table WHERE e_prescription_code = ?";
            String getUserIdSql = "SELECT user_id FROM user_table WHERE pesel = ?";
            String insertPrescriptionSql = "INSERT INTO e_prescription_table (prescription_id, barcode, date_of_issue, e_prescription_code, medicines, doctor_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?);";

            // get Max prescription_id from Database
            preparedStatement = connection.prepareStatement(getMaxPrescriptionIdSql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String maxPrescriptionId = resultSet.getString("max_prescription_id");
            BigInteger maxBarcode = new BigInteger(resultSet.getString("max_barcode"));
            maxBarcode = maxBarcode.add(BigInteger.ONE);

            // Conversion of string to BigInteger, adding 1 and back convert to a string
            String[] parts = maxPrescriptionId.split("\\.");
            BigInteger number = new BigInteger(String.join("", parts));
            number = number.add(BigInteger.ONE);
            String numberIncreased = number.toString();

            // Adding back the dots inside the string
            StringBuilder maxPrescriptionIdIncreased = new StringBuilder();
            int lastIndex = 0;
            for (String part : parts) {
                maxPrescriptionIdIncreased.append(numberIncreased, lastIndex, lastIndex + part.length());
                lastIndex += part.length();
                if (lastIndex < numberIncreased.length()) {
                    maxPrescriptionIdIncreased.append(".");
                }
            }

            // generate e_prescription_code
            int new_e_prescription_code;
            do {
                Random random = new Random();
                new_e_prescription_code = 1000 + random.nextInt(9000); // Random value from 1000 to 9999
                preparedStatement = connection.prepareStatement(checkEPrescriptionCodeSql);
                preparedStatement.setString(1, String.valueOf(new_e_prescription_code));
                resultSet = preparedStatement.executeQuery();
            } while (resultSet.next());

            // get user_id from database
            preparedStatement = connection.prepareStatement(getUserIdSql);
            preparedStatement.setString(1, pesel);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int user_id = resultSet.getInt("user_id");

            // insert into e_prescription_table
            preparedStatement = connection.prepareStatement(insertPrescriptionSql);
            preparedStatement.setString(1, maxPrescriptionIdIncreased.toString());
            preparedStatement.setString(2, maxBarcode.toString());
            preparedStatement.setDate(3, Date.valueOf(date));
            preparedStatement.setString(4, String.valueOf(new_e_prescription_code));
            preparedStatement.setString(5, medicines);
            preparedStatement.setInt(6, Integer.parseInt(doctor_id));
            preparedStatement.setInt(7, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("E-prescription prescribed correctly.");
                returnStatement = "E-prescription prescribed correctly.";
            } else {
                System.out.println("Error in database while prescribing E-prescription.");
                returnStatement = "Error in database while prescribing E-prescription.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Creates and adds 100 new one-time codes to the database.
     *
     * @param clientID the unique identifier of the client initiating the creation of new codes
     * @return a string indicating the status of the operation:
     *         - "100 new one-time codes added correctly." if the codes are successfully added
     *         - "Error in database while adding new one-time codes." if an error occurs during the process
     *         - "Error" if there is any other error during the process
     */
    String createNewOneTimeCodes(int clientID) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);

            String selectMaxCode = "SELECT MAX(one_time_code) FROM one_time_code_table";
            preparedStatement = connection.prepareStatement(selectMaxCode);
            resultSet = preparedStatement.executeQuery();

            int maxCode = 0;
            if (resultSet.next()) {
                maxCode = resultSet.getInt(1);
            }

            // execute the INSERT statement to add 100 new codes
            String insertNewCodes = "INSERT INTO one_time_code_table (one_time_code) VALUES ";
            for (int i = 0; i < 100; i++) {
                maxCode++;
                insertNewCodes += "(LPAD(" + maxCode + ", 6, '0')),";
            }

            // Remove the trailing comma and execute the query
            insertNewCodes = insertNewCodes.substring(0, insertNewCodes.length() - 1);
            preparedStatement = connection.prepareStatement(insertNewCodes);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 100) {
                System.out.println("100 new one-time codes added correctly.");
                returnStatement = "100 new one-time codes added correctly.";
            } else {
                System.out.println("Error in database while adding new one-time codes.");
                returnStatement = "Error in database while adding new one-time codes.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SQL: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Adds a new medical recommendation to the database.
     *
     * @param clientID              the unique identifier of the client initiating the addition of a recommendation
     * @param diet                  the recommended diet
     * @param medicines             the recommended medicines
     * @param nextCheckUpDate       the date of the next medical check-up
     * @param nextCheckUpName       the name of the next medical check-up
     * @param additionalInformation additional information related to the recommendation
     * @param date                  the date of the recommendation
     * @param doctor_id             the unique identifier of the doctor providing the recommendation
     * @param pesel                 the PESEL number of the patient
     * @return a string indicating the status of the operation:
     *         - "Recommendation added correctly." if the recommendation is successfully added
     *         - "Error in database while adding recommendation." if an error occurs during the process
     *         - "Error" if there is any other error during the process
     */
    String addRecommendation(int clientID, String diet, String medicines, String nextCheckUpDate, String nextCheckUpName, String additionalInformation, String date, String doctor_id, String pesel) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getMaxRecommendationsIdSql = "SELECT MAX(recommendation_id) AS max_recommendations_id FROM recommendations_table";
            String getUserIdSql = "SELECT user_id FROM user_table WHERE pesel = ?";
            String addRecommendationSql = "INSERT INTO recommendations_table (recommendation_id, diet, medicines, next_medical_check_up_date, next_medical_check_up_name, additional_information, recommendation_date, doctor_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

            // get Max prescription_id from Database
            preparedStatement = connection.prepareStatement(getMaxRecommendationsIdSql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int maxRecommendationId = resultSet.getInt("max_recommendations_id");

            // get user_id from database
            preparedStatement = connection.prepareStatement(getUserIdSql);
            preparedStatement.setString(1, pesel);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int user_id = resultSet.getInt("user_id");

            // insert into e_prescription_table
            preparedStatement = connection.prepareStatement(addRecommendationSql);
            preparedStatement.setInt(1, maxRecommendationId + 1);

            if (!diet.isEmpty())
                preparedStatement.setString(2, diet);
            else
                preparedStatement.setNull(2, java.sql.Types.VARCHAR);

            if (!medicines.isEmpty())
                preparedStatement.setString(3, medicines);
            else
                preparedStatement.setNull(3, java.sql.Types.VARCHAR);

            if (!nextCheckUpDate.equals("null"))
                preparedStatement.setDate(4, Date.valueOf(nextCheckUpDate));
            else
                preparedStatement.setNull(4, java.sql.Types.DATE);

            if (!nextCheckUpName.isEmpty())
                preparedStatement.setString(5, nextCheckUpName);
            else
                preparedStatement.setNull(5, java.sql.Types.VARCHAR);

            if (!additionalInformation.isEmpty())
                preparedStatement.setString(6, additionalInformation);
            else
                preparedStatement.setNull(6, java.sql.Types.VARCHAR);

            if (!date.equals("null"))
                preparedStatement.setDate(7, Date.valueOf(date));
            else
                preparedStatement.setNull(7, java.sql.Types.DATE);

            preparedStatement.setInt(8, Integer.parseInt(doctor_id));
            preparedStatement.setInt(9, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Recommendation added correctly.");
                returnStatement = "Recommendation added correctly.";
            } else {
                System.out.println("Error in database while adding recommendation.");
                returnStatement = "Error in database while adding recommendation.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Adds a new medical history entry to the database.
     *
     * @param clientID         the unique identifier of the client initiating the addition of a medical history entry
     * @param medicalCase      the description of the medical case
     * @param ICD10FirstLetter the first letter of the ICD10 code associated with the medical case
     * @param ICD10Code        the ICD10 code associated with the medical case
     * @param pesel            the PESEL number of the patient
     * @return a string indicating the status of the operation:
     *         - "Medical history added correctly." if the medical history entry is successfully added
     *         - "Error in database while adding medical history." if an error occurs during the process
     *         - "Error" if there is any other error during the process
     */
    String addMedicalHistory(int clientID, String medicalCase, String ICD10FirstLetter, String ICD10Code, String pesel) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getUserIdFromPeselSql = "SELECT user_id FROM user_table WHERE pesel = ?";
            String addMedicalHistorySql = "INSERT INTO user_medical_history_table (ICD10_first_letter, ICD10_code, medical_case, user_id) VALUES (?, ?, ?, ?);";

            // get user_id from pesel
            preparedStatement = connection.prepareStatement(getUserIdFromPeselSql);
            preparedStatement.setString(1, pesel);
            resultSet = preparedStatement.executeQuery();
            int user_id = resultSet.getInt("user_id");
            ;


            // insert into user_medical_history_table
            preparedStatement = connection.prepareStatement(addMedicalHistorySql);
            preparedStatement.setString(1, ICD10FirstLetter);
            preparedStatement.setString(2, ICD10Code);
            preparedStatement.setString(3, medicalCase);
            preparedStatement.setInt(4, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Medical history added correctly.");
                returnStatement = "Medical history added correctly.";
            } else {
                System.out.println("Error in database while adding medical history.");
                returnStatement = "Error in database while adding medical history.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Adds a new documentation entry to the database.
     *
     * @param clientID          the unique identifier of the client initiating the addition of a documentation entry
     * @param interview         the details of the interview
     * @param physicalExamination the details of the physical examination
     * @param ICD10Code         the ICD10 code associated with the documentation entry
     * @param recommendationId  the ID of the associated recommendation, can be empty
     * @param pesel             the PESEL number of the patient
     * @param date              the date of the documentation entry
     * @param doctor_id         the ID of the doctor creating the documentation entry
     * @return a string indicating the status of the operation:
     *         - "Documentation added correctly." if the documentation entry is successfully added
     *         - "Error in database while adding documentation." if an error occurs during the process
     *         - "User not found with the provided PESEL." if the user is not found in the database
     *         - "Error" if there is any other error during the process
     */
    String addDocumentation(int clientID, String interview, String physicalExamination, String ICD10Code, String recommendationId, String pesel, String date, String doctor_id) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getMaxDocumentationIdSql = "SELECT MAX(documentation_id) AS max_documentation_id FROM documentation_table";
            String getUserIdSql = "SELECT user_id FROM user_table WHERE pesel = ?";
            String addDocumentationSql = "INSERT INTO documentation_table (documentation_id, documentation_date, interview, physical_examination, ICD_10, user_id, doctor_id, recommendations_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

            // get Max documentation_id from Database
            preparedStatement = connection.prepareStatement(getMaxDocumentationIdSql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int maxDocumentationId = resultSet.getInt("max_documentation_id");

            // get user_id from database
            preparedStatement = connection.prepareStatement(getUserIdSql);
            preparedStatement.setString(1, pesel);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int user_id = resultSet.getInt("user_id");

            // insert into documentation_table
            preparedStatement = connection.prepareStatement(addDocumentationSql);
            preparedStatement.setInt(1, maxDocumentationId + 1);
            preparedStatement.setDate(2, Date.valueOf(date));
            preparedStatement.setString(3, interview);
            preparedStatement.setString(4, physicalExamination);

            if (!ICD10Code.equals("null"))
                preparedStatement.setString(5, ICD10Code);
            else
                preparedStatement.setNull(5, java.sql.Types.VARCHAR);

            preparedStatement.setInt(6, user_id);
            preparedStatement.setInt(7, Integer.parseInt(doctor_id));

            if (!recommendationId.isEmpty())
                preparedStatement.setInt(8, Integer.parseInt(recommendationId));
            else
                preparedStatement.setNull(8, java.sql.Types.INTEGER);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Documentation added correctly.");
                returnStatement = "Documentation added correctly.";
            } else {
                System.out.println("Error in database while adding documentation.");
                returnStatement = "Error in database while adding documentation.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Deletes a documentation entry from the database.
     *
     * @param clientID       the unique identifier of the client initiating the deletion of the documentation entry
     * @param documentationId the ID of the documentation entry to be deleted
     * @return a string indicating the status of the operation:
     *         - "Documentation deleted correctly." if the documentation entry is successfully deleted
     *         - "Error in database while deleting documentation." if an error occurs during the deletion process
     *         - "Documentation not found with the provided ID." if the documentation entry is not found in the database
     *         - "Error" if there is any other error during the process
     */
    String deleteDocumentation(int clientID, String documentationId) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String deleteDocumentationSql = "DELETE FROM documentation_table WHERE documentation_id = ?";

            // delete documentation
            preparedStatement = connection.prepareStatement(deleteDocumentationSql);
            preparedStatement.setInt(1, Integer.parseInt(documentationId));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Documentation deleted correctly.");
                returnStatement = "Documentation deleted correctly.";
            } else {
                System.out.println("Error in database while deleting documentation.");
                returnStatement = "Error in database while deleting documentation.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Deletes a medical history entry from the database.
     *
     * @param clientID         the unique identifier of the client initiating the deletion of the medical history entry
     * @param medicalHistoryId the ID of the medical history entry to be deleted
     * @return a string indicating the status of the operation:
     *         - "Medical history deleted correctly." if the medical history entry is successfully deleted
     *         - "Error in database while deleting medical history." if an error occurs during the deletion process
     *         - "Medical history not found with the provided ID." if the medical history entry is not found in the database
     *         - "Error" if there is any other error during the process
     */
    String deleteMedicalHistory(int clientID, String medicalHistoryId) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String deleteMedicalHistorySql = "DELETE FROM user_medical_history_table WHERE medical_history_id = ?";

            // delete medical history
            preparedStatement = connection.prepareStatement(deleteMedicalHistorySql);
            preparedStatement.setInt(1, Integer.parseInt(medicalHistoryId));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Medical history deleted correctly.");
                returnStatement = "Medical history deleted correctly.";
            } else {
                System.out.println("Error in database while deleting medical history.");
                returnStatement = "Error in database while deleting medical history.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Prescribes an electronic referral (E-referral) and adds it to the database.
     *
     * @param clientID       the unique identifier of the client initiating the prescription of the E-referral
     * @param eReferralName  the name or description of the E-referral
     * @param date           the date of issue for the E-referral
     * @param doctor_id      the ID of the doctor prescribing the E-referral
     * @param pesel          the PESEL (Personal Identification Number) of the patient receiving the E-referral
     * @return a string indicating the status of the operation:
     *         - "E-referral prescribed correctly." if the E-referral is successfully prescribed and added to the database
     *         - "Error in database prescribing E-referral." if an error occurs during the prescription process
     *         - "Error" if there is any other error during the process
     */
    String prescribeEReferral(int clientID, String eReferralName, String date, String doctor_id, String pesel) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getMaxReferralIdSql = "SELECT MAX(referral_id) AS max_referral_id, MAX(barcode) AS max_barcode FROM e_referral_table";
            String checkEReferralCodeSql = "SELECT e_referral_code FROM e_referral_table WHERE e_referral_code = ?";
            String getUserIdSql = "SELECT user_id FROM user_table WHERE pesel = ?";
            String insertReferralSql = "INSERT INTO e_referral_table (referral_id, barcode, date_of_issue, e_referral_code, referral_name, doctor_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?);";

            // get Max prescription_id from Database
            preparedStatement = connection.prepareStatement(getMaxReferralIdSql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            String maxReferralId = resultSet.getString("max_referral_id");
            BigInteger maxBarcode = new BigInteger(resultSet.getString("max_barcode"));
            maxBarcode = maxBarcode.add(BigInteger.ONE);

            // Conversion of string to BigInteger, adding 1 and back convert to a string
            String[] parts = maxReferralId.split("\\.");
            BigInteger number = new BigInteger(String.join("", parts));
            number = number.add(BigInteger.ONE);
            String numberIncreased = number.toString();

            // Adding back the dots inside the string
            StringBuilder maxReferralIdIncreased = new StringBuilder();
            int lastIndex = 0;
            for (String part : parts) {
                maxReferralIdIncreased.append(numberIncreased, lastIndex, lastIndex + part.length());
                lastIndex += part.length();
                if (lastIndex < numberIncreased.length()) {
                    maxReferralIdIncreased.append(".");
                }
            }

            // generate e_referral_code
            int new_e_referral_code;
            do {
                Random random = new Random();
                new_e_referral_code = 1000 + random.nextInt(9000); // Random value from 1000 to 9999
                preparedStatement = connection.prepareStatement(checkEReferralCodeSql);
                preparedStatement.setString(1, String.valueOf(new_e_referral_code));
                resultSet = preparedStatement.executeQuery();
            } while (resultSet.next());

            // get user_id from database
            preparedStatement = connection.prepareStatement(getUserIdSql);
            preparedStatement.setString(1, pesel);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int user_id = resultSet.getInt("user_id");

            // insert into e_referral_table
            preparedStatement = connection.prepareStatement(insertReferralSql);
            preparedStatement.setString(1, maxReferralIdIncreased.toString());
            preparedStatement.setString(2, maxBarcode.toString());
            preparedStatement.setDate(3, Date.valueOf(date));
            preparedStatement.setString(4, String.valueOf(new_e_referral_code));
            preparedStatement.setString(5, eReferralName);
            preparedStatement.setInt(6, Integer.parseInt(doctor_id));
            preparedStatement.setInt(7, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("E-referral prescribed correctly.");
                returnStatement = "E-referral prescribed correctly.";
            } else {
                System.out.println("Error in database prescribing E-referral.");
                returnStatement = "Error in database prescribing E-referral.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Updates the settings for a doctor in the database.
     *
     * @param clientID                 the unique identifier of the client initiating the settings update
     * @param doctor_id               the ID of the doctor whose settings need to be updated
     * @param bmi_setting             the new BMI setting value ("true" or "false")
     * @param age_setting             the new age setting value ("true" or "false")
     * @param currentDate_setting     the new current date setting value ("true" or "false")
     * @param weightInChart_setting   the new weight in chart setting value ("true" or "false")
     * @param temperatureInChart_setting the new temperature in chart setting value ("true" or "false")
     * @return a string indicating the status of the operation:
     *         - "Settings changed correctly." if the settings are successfully updated in the database
     *         - "Error in database while setting settings." if an error occurs during the update process
     *         - "Error" if there is any other error during the process
     */
    String setDoctorSettings(int clientID, String doctor_id, String bmi_setting, String age_setting, String currentDate_setting, String weightInChart_setting, String temperatureInChart_setting) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "UPDATE doctor_settings_table SET bmi_setting = ?, age_setting = ?, currentDate_setting = ?, weightInChart_setting = ?, temperatureInChart_setting = ? WHERE doctor_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, bmi_setting);
            preparedStatement.setString(2, age_setting);
            preparedStatement.setString(3, currentDate_setting);
            preparedStatement.setString(4, weightInChart_setting);
            preparedStatement.setString(5, temperatureInChart_setting);
            preparedStatement.setInt(6, Integer.parseInt(doctor_id));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Settings changed correctly.");
                returnStatement = "Settings changed correctly.";

            } else {
                System.out.println("Error in database while setting settings.");
                returnStatement = "Error in database while setting settings.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * The getClinics method is responsible for retrieving the clinics from the database.
     *
     * @param clientID the ID of the client
     * @return a 2D array of strings containing the clinics
     */
    String[][] getClinics(int clientID) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM clinic_table";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            if (rowCount <= 0) {
                System.out.println("Error in database while getting list of clinics.");
                returnStatement[0][0] = "Error";
                return returnStatement;
            }

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * The getExaminations method is responsible for retrieving the examinations of a user.
     *
     * @param clientID the ID of the client
     * @param user_id  the ID of the user
     * @return a 2D array of strings containing the examinations
     */
    String[][] getExaminations(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT examination_nr, name, examination_date, doctor_table.first_name, doctor_table.last_name, doctor_table.phone FROM examination_table INNER JOIN doctor_table ON examination_table.doctor_id = doctor_table.doctor_id WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No examinations in database.");
                return new String[][]{{"No examinations in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * The getMessages method is responsible for retrieving the messages of a user.
     *
     * @param clientID the ID of the client
     * @param user_id  the ID of the user
     * @return a 2D array of strings containing the messages
     */
    String[][] getNotifications(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM messages_table WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No notifications in database.");
                return new String[][]{{"No notifications in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves notifications for a doctor from the database.
     *
     * @param clientID   the unique identifier of the client initiating the notification retrieval
     * @param doctor_id  the ID of the doctor for whom notifications need to be retrieved
     * @return a 2D array containing the notifications or a single-element array with an error message:
     *         - {"No notifications in database"} if there are no notifications
     *         - {{notification1_col1, notification1_col2, ...}, {notification2_col1, notification2_col2, ...}, ...} if notifications are found
     *         - null if there is an error during the process
     */
    String[][] getDoctorNotifications(int clientID, int doctor_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM doctor_message_table WHERE doctor_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, doctor_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No notifications in database.");
                return new String[][]{{"No notifications in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves E-Referrals for a user from the database.
     *
     * @param clientID the unique identifier of the client initiating the E-Referral retrieval
     * @param user_id  the ID of the user for whom E-Referrals need to be retrieved
     * @return a 2D array containing the E-Referrals or a single-element array with an error message:
     *         - {"No EReferrals in database"} if there are no E-Referrals
     *         - {{"EReferral1_col1", "EReferral1_col2", ...}, {"EReferral2_col1", "EReferral2_col2", ...}, ...} if E-Referrals are found
     *         - null if there is an error during the process
     */
    String[][] getEReferral(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT e_referral_table.*, doctor_table.first_name, doctor_table.last_name, doctor_table.phone FROM e_referral_table INNER JOIN doctor_table ON e_referral_table.doctor_id = doctor_table.doctor_id WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No EReferrals in database.");
                return new String[][]{{"No EReferrals in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves findings for a user from the database.
     *
     * @param clientID the unique identifier of the client initiating the findings retrieval
     * @param user_id  the ID of the user for whom findings need to be retrieved
     * @return a 2D array containing the findings or a single-element array with an error message:
     *         - {"No findings in database"} if there are no findings
     *         - {{"Finding1_col1", "Finding1_col2", ...}, {"Finding2_col1", "Finding2_col2", ...}, ...} if findings are found
     *         - null if there is an error during the process
     */
    String[][] getFindings(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM findings_table WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No findings in database.");
                return new String[][]{{"No findings in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves findings for a user identified by their PESEL from the database.
     *
     * @param clientID the unique identifier of the client initiating the findings retrieval
     * @param pesel    the PESEL of the user for whom findings need to be retrieved
     * @return a 2D array containing the findings or a single-element array with an error message:
     *         - {"No findings in database"} if there are no findings
     *         - {{"Finding1_col1", "Finding1_col2", ...}, {"Finding2_col1", "Finding2_col2", ...}, ...} if findings are found
     *         - null if there is an error during the process
     */
    String[][] getDoctorFindings(int clientID, String pesel) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM findings_table LEFT JOIN user_table ON findings_table.user_id = user_table.user_id WHERE user_table.pesel = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, pesel);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No findings in database.");
                return new String[][]{{"No findings in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves e-prescriptions for a user identified by their user ID from the database.
     *
     * @param clientID the unique identifier of the client initiating the e-prescription retrieval
     * @param user_id  the user ID for whom e-prescriptions need to be retrieved
     * @return a 2D array containing the e-prescriptions or a single-element array with an error message:
     *         - {"No EPrescriptions in database"} if there are no e-prescriptions
     *         - {{"EPrescription1_col1", "EPrescription1_col2", ...}, {"EPrescription2_col1", "EPrescription2_col2", ...}, ...} if e-prescriptions are found
     *         - null if there is an error during the process
     */
    String[][] getEPrescrition(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT e_prescription_table.*, doctor_table.first_name, doctor_table.last_name, doctor_table.phone FROM e_prescription_table INNER JOIN doctor_table ON e_prescription_table.doctor_id = doctor_table.doctor_id WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No EPrescriptions in database.");
                return new String[][]{{"No EPrescriptions in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves recommendations for a user identified by their user ID from the database.
     *
     * @param clientID the unique identifier of the client initiating the recommendation retrieval
     * @param user_id  the user ID for whom recommendations need to be retrieved
     * @return a 2D array containing the recommendations or a single-element array with an error message:
     *         - {"No recommendations in the database"} if there are no recommendations
     *         - {{"Recommendation1_col1", "Recommendation1_col2", ...}, {"Recommendation2_col1", "Recommendation2_col2", ...}, ...} if recommendations are found
     *         - null if there is an error during the process
     */
    String[][] getRecommendation(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT recommendations_table.*, doctor_table.first_name, doctor_table.last_name, doctor_table.phone FROM recommendations_table INNER JOIN doctor_table ON recommendations_table.doctor_id = doctor_table.doctor_id WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No recommendations in database.");
                return new String[][]{{"No recommendations in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves Short Medical Interview (SMI) details for a user identified by their registration number and PESEL from the database.
     *
     * @param clientID         the unique identifier of the client initiating the SMI retrieval
     * @param registration_nr  the registration number associated with the SMI
     * @param pesel            the PESEL of the user for whom SMI details need to be retrieved
     * @return an array containing SMI details or an array with an error message:
     *         - {"SMI with this code doesn't exist in this user!"} if the SMI with the specified code doesn't exist for the user
     *         - {"Error", "Error", ..., "Error"} if there is an error during the process
     *         - {"registration_nr", "what_hurts_you", "pain_symptoms", "other_symptoms", "symptoms_other_symptoms", "medicines", "extent_of_pain", "when_the_pain_started", "temperature", "additional_description", "result_smi", "smi_date", "user_id"} if SMI details are found
     */
    String[] getSMI(int clientID, int registration_nr, String pesel) {
        String[] returnStatement = {"Error", "Error", "Error", "Error", "Error", "Error", "Error", "Error", "Error", "Error", "Error", "Error", "Error"};
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT short_medical_interview_table.* FROM short_medical_interview_table LEFT JOIN user_table ON short_medical_interview_table.user_id = user_table.user_id WHERE registration_nr = ? AND user_table.pesel = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, registration_nr);
            preparedStatement.setString(2, pesel);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                returnStatement[0] = String.valueOf(resultSet.getInt("registration_nr"));
                returnStatement[1] = resultSet.getString("what_hurts_you");
                returnStatement[2] = resultSet.getString("pain_symptoms");
                returnStatement[3] = resultSet.getString("other_symptoms");
                returnStatement[4] = resultSet.getString("symptoms_other_symptoms");
                returnStatement[5] = resultSet.getString("medicines");
                returnStatement[6] = resultSet.getString("extent_of_pain");
                returnStatement[7] = resultSet.getString("when_the_pain_started");
                returnStatement[8] = String.valueOf(resultSet.getDouble("temperature"));
                returnStatement[9] = resultSet.getString("additional_description");
                returnStatement[10] = resultSet.getString("result_smi");
                returnStatement[11] = String.valueOf(resultSet.getDate("smi_date"));
                returnStatement[12] = String.valueOf(resultSet.getInt("user_id"));
            } else {
                System.out.println("SMI with this code doesn't exist in this user!");
                return new String[]{"SMI with this code doesn't exist in this user!"};
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves medical history details for a user identified by their PESEL from the database.
     *
     * @param clientID  the unique identifier of the client initiating the medical history retrieval
     * @param pesel     the PESEL of the user for whom medical history details need to be retrieved
     * @return a 2D array containing medical history details or a 2D array with a single-row containing an error message:
     *         - {{"No medical history in database"}} if there is no medical history for the user
     *         - {{"Error"}} if there is an error during the process
     *         - {{"column1", "column2", ..., "columnN"}, ..., {"value1", "value2", ..., "valueN"}} if medical history details are found
     */
    String[][] getDoctorMedicalHistory(int clientID, String pesel) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM user_medical_history_table LEFT JOIN user_table ON user_medical_history_table.user_id = user_table.user_id WHERE user_table.pesel = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, pesel);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No medical history in database.");
                return new String[][]{{"No medical history in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves medical history details for a user identified by their user ID from the database.
     *
     * @param clientID  the unique identifier of the client initiating the medical history retrieval
     * @param user_id   the user ID for whom medical history details need to be retrieved
     * @return a 2D array containing medical history details or a 2D array with a single-row containing an error message:
     *         - {{"No medical history in database"}} if there is no medical history for the user
     *         - {{"Error"}} if there is an error during the process
     *         - {{"column1", "column2", ..., "columnN"}, ..., {"value1", "value2", ..., "valueN"}} if medical history details are found
     */
    String[][] getMedicalHistory(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM user_medical_history_table WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No medical history in database.");
                return new String[][]{{"No medical history in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves documentation details for a user identified by their PESEL from the database.
     *
     * @param clientID  the unique identifier of the client initiating the documentation retrieval
     * @param pesel     the PESEL of the user for whom documentation details need to be retrieved
     * @return a 2D array containing documentation details or a 2D array with a single-row containing an error message:
     *         - {{"No documentations in database"}} if there is no documentation for the user
     *         - {{"Error"}} if there is an error during the process
     *         - {{"column1", "column2", ..., "columnN"}, ..., {"value1", "value2", ..., "valueN"}} if documentation details are found
     */
    String[][] getDocumentations(int clientID, String pesel) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM documentation_table LEFT JOIN user_table ON documentation_table.user_id = user_table.user_id WHERE user_table.pesel = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, pesel);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No documentations in database.");
                return new String[][]{{"No documentations in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves blood pressure records for a user identified by their user ID from the database.
     *
     * @param clientID  the unique identifier of the client initiating the blood pressure record retrieval
     * @param user_id   the user ID of the user for whom blood pressure records need to be retrieved
     * @return a 2D array containing blood pressure records or a 2D array with a single-row containing an error message:
     *         - {{"No pressures in database"}} if there are no blood pressure records for the user
     *         - {{"Error"}} if there is an error during the process
     *         - {{"column1", "column2", ..., "columnN"}, ..., {"value1", "value2", ..., "valueN"}} if blood pressure records are found
     */
    String[][] getPressure(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM user_basic_data_table WHERE user_id = ? ORDER BY entry_date";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No pressures in database.");
                return new String[][]{{"No pressures in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * The checkDataBase method is responsible for checking the database.
     */
    public void checkDataBase() {
        Connection connection = null;
        try {
            // EMERGENCY !!!
            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
            connection = DriverManager.getConnection(url, DBusername, DBpassword);

            if (connection != null)
                System.out.println(Color.ColorString("DB engine created correctly.", Color.ANSI_GREEN));
        } catch (SQLException e) {
            if (e.getSQLState().equals("08S01")) {
                System.out.println(Color.ColorString("DB engine cannot be created. Something went wrong!", Color.ANSI_RED));
                System.exit(-1);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println(Color.ColorString(e.getMessage(), Color.ANSI_RED));
            e.printStackTrace();
            System.exit(-2);
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error while closing resources: " + e.getMessage());
            }
        }
    }

    /**
     * Adds a short medical interview record to the database.
     *
     * @param clientID                  the unique identifier of the client initiating the addition of the short medical interview record
     * @param what_hurts_you            the description of what hurts the user
     * @param pain_symptoms             the description of pain symptoms (can be empty)
     * @param other_symptoms            the description of other symptoms (can be empty)
     * @param symptoms_other_symptoms   the description of the relationship between pain symptoms and other symptoms (can be empty)
     * @param medicines                 the list of medicines
     * @param extent_of_pain            the extent of pain
     * @param when_the_pain_started     information about when the pain started
     * @param temperature               the recorded temperature
     * @param additional_description    any additional description related to the short medical interview
     * @param result_smi                the result of the short medical interview
     * @param smi_date                  the date of the short medical interview
     * @param user_id                   the user ID associated with the short medical interview
     * @return a string indicating the outcome of the operation:
     *         - "SMI added correctly with nr: [registration_nr]" if the short medical interview record is added successfully
     *         - "Error in database while adding SMI." if there is an error during the process
     */
    String addShortMedicalInterview(int clientID, String what_hurts_you, String pain_symptoms, String other_symptoms, String symptoms_other_symptoms, String medicines, String extent_of_pain, String when_the_pain_started, String temperature, String additional_description, String result_smi, String smi_date, int user_id) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getMaxRegistrationNrSql = "SELECT MAX(registration_nr) AS max_registration_nr FROM short_medical_interview_table";
            String addSMISql = "INSERT INTO short_medical_interview_table (registration_nr, what_hurts_you, pain_symptoms, other_symptoms, symptoms_other_symptoms, medicines, extent_of_pain, when_the_pain_started, temperature, additional_description, result_smi, smi_date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

            // get Max registration_nr from Database
            preparedStatement = connection.prepareStatement(getMaxRegistrationNrSql);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int maxRegistrationNr = resultSet.getInt("max_registration_nr");

            // insert into short_medical_interview_table
            preparedStatement = connection.prepareStatement(addSMISql);
            preparedStatement.setInt(1, maxRegistrationNr + 1);
            preparedStatement.setString(2, what_hurts_you);

            if (!pain_symptoms.isEmpty()) {
                preparedStatement.setString(3, pain_symptoms);
            } else {
                preparedStatement.setNull(3, java.sql.Types.VARCHAR);
            }
            if (!other_symptoms.isEmpty())
                preparedStatement.setString(4, other_symptoms);
            else
                preparedStatement.setNull(4, java.sql.Types.VARCHAR);

            if (!symptoms_other_symptoms.isEmpty())
                preparedStatement.setString(5, symptoms_other_symptoms);
            else
                preparedStatement.setNull(5, java.sql.Types.VARCHAR);

            preparedStatement.setString(6, medicines);
            preparedStatement.setString(7, extent_of_pain);
            preparedStatement.setString(8, when_the_pain_started);
            preparedStatement.setDouble(9, Double.parseDouble(temperature));
            preparedStatement.setString(10, additional_description);
            preparedStatement.setString(11, result_smi);
            preparedStatement.setDate(12, Date.valueOf(smi_date));
            preparedStatement.setInt(13, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("SMI added correctly with nr: " + (maxRegistrationNr + 1));
                returnStatement = "SMI added correctly with nr: " + (maxRegistrationNr + 1);
            } else {
                System.out.println("Error in database while adding SMI.");
                returnStatement = "Error in database while adding SMI.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Adds a short medical interview e-referral record to the database.
     *
     * @param clientID          the unique identifier of the client initiating the addition of the short medical interview e-referral record
     * @param referral_id       the referral ID associated with the e-referral
     * @param barcode           the barcode associated with the e-referral
     * @param date_of_issue     the date of issue of the e-referral
     * @param e_referral_code   the e-referral code
     * @param referral_name     the name of the referral
     * @param doctor_id         the doctor ID associated with the e-referral
     * @param user_id           the user ID associated with the e-referral
     * @return a string indicating the outcome of the operation:
     *         - "SMI E-Referral added correctly." if the short medical interview e-referral record is added successfully
     *         - "Error in database while adding SMI E-Referral." if there is an error during the process
     */
    String addSMIEreferral(int clientID, String referral_id, String barcode, String date_of_issue, String e_referral_code, String referral_name, int doctor_id, int user_id) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String addSMISql = "INSERT INTO e_referral_table (referral_id, barcode, date_of_issue, e_referral_code, referral_name, doctor_id, user_id) VALUES (?, ?, ?, ?, ?, ?, ?);";

            // insert into e_referral_table
            preparedStatement = connection.prepareStatement(addSMISql);
            preparedStatement.setInt(1, Integer.parseInt(referral_id));
            preparedStatement.setInt(2, Integer.parseInt(barcode));
            preparedStatement.setDate(3, Date.valueOf(date_of_issue));
            preparedStatement.setInt(4, Integer.parseInt(e_referral_code));
            preparedStatement.setString(5, referral_name);
            preparedStatement.setInt(6, doctor_id);
            preparedStatement.setInt(7, user_id);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("SMI E-Referral added correctly.");
                returnStatement = "SMI E-Referral added correctly.";
            } else {
                System.out.println("Error in database while adding SMI E-Referral.");
                returnStatement = "Error in database while adding SMI E-Referral.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Retrieves the last blood pressure check date and checks if it was done within the last 3 days.
     *
     * @param clientID the unique identifier of the client initiating the retrieval and check
     * @param user_id  the user ID for whom to retrieve the last blood pressure check date
     * @return a string indicating the outcome of the operation:
     *         - "User checked blood pressure less than 3 days ago." if the last blood pressure check was done within the last 3 days
     *         - "User didn't check blood pressure less than 3 days ago." if the last blood pressure check was done more than 3 days ago
     *         - "Error while checking last blood pressure check." if there is an error during the process
     */
    String getLastBloodPressureCheck(int clientID, int user_id) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String getLastBloodPressureCheckSql = "SELECT * FROM user_basic_data_table WHERE user_id = ? ORDER BY entry_date DESC LIMIT 1;";

            preparedStatement = connection.prepareStatement(getLastBloodPressureCheckSql);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                LocalDateTime lastBloodPressureCheck = resultSet.getTimestamp("entry_date").toLocalDateTime();
                LocalDateTime currentDate = LocalDateTime.now();
                long daysBetween = ChronoUnit.DAYS.between(lastBloodPressureCheck, currentDate);
                if (daysBetween > 3) {
                    System.out.println("User didn't checked blood pressure less than 3 days ago.");
                    returnStatement = "User didn't checked blood pressure less than 3 days ago.";
                } else {
                    System.out.println("User checked blood pressure less than 3 days ago.");
                    returnStatement = "User checked blood pressure less than 3 days ago.";
                }
            } else {
                System.out.println("Error while checking last blood pressure check.");
                returnStatement = "Error while checking last blood pressure check.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * Retrieves E-Contact information for a user from the database.
     *
     * @param clientID the unique identifier of the client initiating the retrieval
     * @param user_id  the user ID for whom to retrieve E-Contact information
     * @return a 2D array containing the E-Contact information:
     *         - Each row represents a record.
     *         - Each column represents a field of the record (first_name, last_name, profession, name, examination_date, meeting_link).
     *         - If no E-Contact information is found, a single-row array with a message "No EContact information in database" is returned.
     */
    String[][] getEContact(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT d.first_name, d.last_name, d.profession, e.name, e.examination_date, e.meeting_link FROM examination_table e JOIN doctor_table d ON e.doctor_id = d.doctor_id WHERE e.user_id = ? AND e.name LIKE '%E-Contact%' AND e.examination_date >= DATE_ADD(NOW(), INTERVAL 30 MINUTE) ORDER BY e.examination_date ASC LIMIT 1;";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No EContact information in database.");
                return new String[][]{{"No EContact information in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Retrieves examinations scheduled for today for a specific doctor from the database.
     *
     * @param clientID  the unique identifier of the client initiating the retrieval
     * @param doctor_id the doctor ID for whom to retrieve examinations
     * @return a 2D array containing examination information scheduled for today:
     *         - Each row represents a record.
     *         - Each column represents a field of the record (examination_nr, name, examination_date, first_name, last_name, phone).
     *         - If no examinations are found, a single-row array with a message "No examinations in database" is returned.
     */
    String[][] getExaminationsForToday(int clientID, int doctor_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT examination_nr, name, examination_date, user_table.first_name, user_table.last_name, user_table.phone FROM examination_table INNER JOIN user_table ON examination_table.user_id = user_table.user_id WHERE doctor_id = ? AND DATE(examination_date) = CURRENT_DATE;";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, doctor_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No examinations in database.");
                return new String[][]{{"No examinations in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }

    /**
     * Adds a meeting link to a specific examination in the database.
     *
     * @param clientID        the unique identifier of the client initiating the operation
     * @param examination_nr  the examination number for which to add the meeting link
     * @param link            the meeting link to be added
     * @return a string indicating the outcome of the operation:
     *         - If the link is added successfully, it returns "Examination link added correctly."
     *         - If there is an error in the database, it returns "Error in database while adding Examination link."
     */
    String addExaminationLink(int clientID, String examination_nr, String link) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String addSMISql = "UPDATE examination_table SET meeting_link = ? WHERE examination_nr = ?;";

            // insert link into examination_table
            preparedStatement = connection.prepareStatement(addSMISql);
            preparedStatement.setString(1, link);
            preparedStatement.setInt(2, Integer.parseInt(examination_nr));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 1) {
                System.out.println("Examination link added correctly.");
                returnStatement = "Examination link added correctly.";
            } else {
                System.out.println("Error in database while adding Examination link.");
                returnStatement = "Error in database while adding Examination link.";
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return returnStatement;
    }

    /**
     * The getExaminations method is responsible for retrieving the examinations of a user.
     *
     * @param clientID  the ID of the client
     * @param doctor_id the ID of the doctor
     * @return a 2D array of strings containing the examinations
     */
    String[][] getDoctorExaminations(int clientID, int doctor_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT examination_nr, name, examination_date, user_table.first_name, user_table.last_name, user_table.phone FROM examination_table INNER JOIN user_table ON examination_table.user_id = user_table.user_id WHERE doctor_id = ? AND DATE(examination_date) >= CURDATE() - INTERVAL 1 DAY;";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, doctor_id);

            resultSet = preparedStatement.executeQuery();

            int rowCount = 0;
            while (resultSet.next()) {
                rowCount++;
            }

            if (rowCount == 0) {
                System.out.println("No examinations in database.");
                return new String[][]{{"No examinations in database"}};
            }

            int columnCount = resultSet.getMetaData().getColumnCount();
            String[][] returnStatement = new String[rowCount][columnCount];

            resultSet.beforeFirst(); // Go back to begin of ResultSet
            int row = 0;
            while (resultSet.next()) {
                for (int col = 0; col < columnCount; col++) {
                    returnStatement[row][col] = resultSet.getString(col + 1);
                }
                row++;
            }

            return returnStatement;

        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return null;
    }
}
