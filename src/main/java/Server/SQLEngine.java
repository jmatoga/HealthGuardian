package Server;

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
     * @param host the host of the database
     * @param port the port of the database
     * @param database the name of the database
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
     * @param connection the Connection object
     * @param clientId the ID of the client
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
     * @param clientID the ID of the client
     * @param user_id the ID of the user
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

            if(resultSet.next()) {
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

            if(resultSet.next()) {
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
     * @param clientID the ID of the client
     * @param username the username of the user
     * @param email the email of the user
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
     * @param clientID the ID of the client
     * @param oneTimeCode the one-time code of the user
     * @param firstname the first name of the user
     * @param lastname the last name of the user
     * @param email the email of the user
     * @param phoneNumber the phone number of the user
     * @param pesel the PESEL number of the user
     * @param username the username of the user
     * @param password the password of the user
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
     * @param clientID the ID of the client
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
     * @param clientID the ID of the client
     * @param birthdayDate the birthday date of the user
     * @param weight the weight of the user
     * @param height the height of the user
     * @param temperature the temperature of the user
     * @param systolic_pressure the systolic pressure of the user
     * @param diastolic_pressure the diastolic pressure of the user
     * @param entryDate the entry date of the user
     * @param userId the ID of the user
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
     * @param resultSet the ResultSet object to be closed
     * @param preparedStatement the PreparedStatement object to be closed
     * @param connection the Connection object to be closed
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
     * @param resultSet the first ResultSet object to be closed
     * @param resultSet1 the second ResultSet object to be closed
     * @param preparedStatement the PreparedStatement object to be closed
     * @param connection the Connection object to be closed
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
     * @param clientID the ID of the client
     * @param user_id the ID of the user
     * @return an array of strings containing the settings
     */
    String[] getSettings(int clientID, String user_id) {
        String[] returnStatement = {"Error", "Error", "Error", "Error", "Error"};

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
     * @param clientID the ID of the client
     * @param user_id the ID of the user
     * @param bmi_setting the BMI setting of the user
     * @param age_setting the age setting of the user
     * @param currentDate_setting the current date setting of the user
     * @param weightInChart_setting the fourth setting of the user
     * @param temperatureInChart_setting the fifth setting of the user
     * @return a string indicating the status of the set operation
     */
    String setSettings(int clientID, String user_id, String bmi_setting, String age_setting, String currentDate_setting, String weightInChart_setting, String temperatureInChart_setting) {
        String returnStatement = "Error";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "UPDATE settings_table SET bmi_setting = ?, age_setting = ?, currentDate_setting = ?, weightInChart_setting = ?, temperatureInChart_setting = ? WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, bmi_setting);
            preparedStatement.setString(2, age_setting);
            preparedStatement.setString(3, currentDate_setting);
            preparedStatement.setString(4, weightInChart_setting);
            preparedStatement.setString(5, temperatureInChart_setting);
            preparedStatement.setInt(6, Integer.parseInt(user_id));

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

            // insert into user table
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
                System.out.println("Error in database prescribing E-prescription.");
                returnStatement = "Error in database prescribing E-prescription.";
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
     * @param clientID the ID of the client
     * @param user_id the ID of the user
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
     * @param clientID the ID of the client
     * @param user_id the ID of the user
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
}
