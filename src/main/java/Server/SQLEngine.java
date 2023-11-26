package Server;

import utils.Color;

import java.sql.*;
import java.time.LocalDate;

public class SQLEngine {
    private final String url;
    private final String DBusername;
    private final String DBpassword;

    public SQLEngine(String host, int port, String database, String DBusername, String DBpassword) {
        // old (local) database //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.DBusername = DBusername;
        this.DBpassword = DBpassword;
    }

    public Connection connectToDataBase(Connection connection, int clientId) {
        try {
            connection = DriverManager.getConnection(url, DBusername, DBpassword);
            System.out.println(Color.ColorString("Database connection successful. (For ClientID: " + clientId + ")", Color.ANSI_GREEN_BACKGROUND));
        } catch (SQLException e) {
            if(e.getSQLState().equals("08S01")) {
                System.out.println(Color.ColorString("ERROR! No internet connection!", Color.ANSI_RED));
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

    String[] getData(int clientID, String user_id){
        String[] returnStatement = {"Error", "Error", "No data", "No data", "No data", "No data", "No data", "No data" , "No data"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT first_name, last_name, birth_date, user_basic_data_table.weight, user_basic_data_table.height, user_basic_data_table.temperature, user_basic_data_table.systolic_pressure, user_basic_data_table.diastolic_pressure, user_basic_data_table.entry_date FROM user_table LEFT JOIN user_basic_data_table on user_table.user_id = user_basic_data_table.user_id AND (SELECT MAX(entry_date) FROM user_basic_data_table WHERE user_id = user_table.user_id) WHERE user_table.user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user_id);

            resultSet = preparedStatement.executeQuery();

            boolean ifResultSetHasNext = resultSet.next();

            returnStatement[0] = String.valueOf(resultSet.getString("first_name"));
            returnStatement[1] = String.valueOf(resultSet.getString("last_name"));

            if (ifResultSetHasNext && resultSet.getDate("birth_date") != null) {
                System.out.println("Getted User Data.");

                returnStatement[2] = String.valueOf(resultSet.getDate("birth_date"));
                returnStatement[3] = String.valueOf(resultSet.getString("user_basic_data_table.weight"));
                returnStatement[4] = String.valueOf(resultSet.getString("user_basic_data_table.height"));
                returnStatement[5] = String.valueOf(resultSet.getString("user_basic_data_table.temperature"));
                returnStatement[6] = String.valueOf(resultSet.getString("user_basic_data_table.systolic_pressure"));
                returnStatement[7] = String.valueOf(resultSet.getString("user_basic_data_table.diastolic_pressure"));
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

    boolean checkIfUserExists(int clientID, String username){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT username FROM user_pass_table WHERE username = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);  // username

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if username exist in database
                System.out.println("User exists!");
                return true;
            } else {
                System.out.println("Correct, username free to use.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return true;
    }

    String checkOneTimeCode(int clientID, String oneTimeCode, String firstname, String lastname, String email, String phoneNumber, String pesel, String username, String password){
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
            if(resultSet.next()) {
                rowCount = resultSet.getInt(1);
            }

            if(rowCount >= 0 && rowCount <= 4) {
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
                if(!birthdayDate.equals("withoutBirthdayDate")) {
                    preparedStatement = connection.prepareStatement(updateUserTableSql);
                    preparedStatement.setDate(1, Date.valueOf(birthdayDate));
                    preparedStatement.setInt(2, Integer.parseInt(userId));
                    rowsAffected1 = preparedStatement.executeUpdate();
                } else
                    rowsAffected1 = 1;

                if(rowsAffected == 1 && rowsAffected1 == 1) {
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

                if(rowsAffected == 1) {
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

    private static void disconnectFromDataBase(ResultSet resultSet, PreparedStatement preparedStatement, Connection connection) {
        // Closing resources (ResultSet, PreparedStatement, Connection)
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
            System.out.println(Color.ColorString("Database connection closed successfully.\n", Color.ANSI_GREEN));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error while closing resources: " + e.getMessage());
        }
    }
}
