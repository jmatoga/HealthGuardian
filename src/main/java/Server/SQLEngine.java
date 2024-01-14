package Server;

import Client.Client;
import utils.Color;

import java.math.BigInteger;
import java.sql.*;
import java.util.Arrays;
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
                String insertSettingsSql = "INSERT INTO settings_table (bmi_setting, age_setting, currentDate_setting, settings_no_4, settings_no_5, user_id) VALUES ('false', 'false', 'false', 'false', 'false', ?)";

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

    String addMedicalHistory(int clientID, String medicalCase, String ICD10FirstLetter, String ICD10Code, String pesel) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String addMedicalHistorySql = "INSERT INTO user_medical_history_table (ICD10_first_letter, ICD10_code, medical_case, user_id) VALUES (?, ?, ?, ?);";

            // get user_id from pesel
            int user_id = getUserIdFromPesel(pesel);

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

            if(resultSet.next()) {
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

    int getUserIdFromPesel(String pesel) {
        String sql = "SELECT user_id FROM user_table WHERE pesel = ?";

        try (Connection connection = DriverManager.getConnection(url, DBusername, DBpassword);
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ) {

            preparedStatement.setString(1, pesel);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("user_id");
                } else {
                    System.out.println("Error in database while getting user_id from pesel.");
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        }

        return -1;
    }

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

    String[][] getDocumentations(int clientID, int user_id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT * FROM documentation_table WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setInt(1, user_id);

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
