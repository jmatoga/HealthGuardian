package Server;

import utils.Color;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

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
        String[] returnStatement = {"Error", "Error", "No data", "No data", "No data", "No data", "No data"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT first_name, last_name, birth_date, user_basic_data_table.weight, user_basic_data_table.height, user_basic_data_table.systolic_pressure, user_basic_data_table.diastolic_pressure FROM user_table NATURAL JOIN user_basic_data_table WHERE user_id = ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user_id);

            resultSet = preparedStatement.executeQuery();

            boolean ifResultSetHasNext = resultSet.next();
            returnStatement[0] = String.valueOf(resultSet.getString("first_name"));
            returnStatement[1] = String.valueOf(resultSet.getString("last_name"));

            if (ifResultSetHasNext && resultSet.getDate("birth_date") != null) {
                System.out.println("Getted User Data.");

                returnStatement[2] = String.valueOf(resultSet.getDate("birth_date").toLocalDate().until(LocalDate.now()).getYears());
                returnStatement[3] = String.valueOf(resultSet.getString("user_basic_data_table.weight"));
                returnStatement[4] = String.valueOf(resultSet.getString("user_basic_data_table.height"));
                returnStatement[5] = String.valueOf(resultSet.getString("user_basic_data_table.systolic_pressure"));
                returnStatement[6] = String.valueOf(resultSet.getString("user_basic_data_table.diastolic_pressure"));

            } else if (ifResultSetHasNext) {
                System.out.println("First login! Insert data.");
            } else {
                System.out.println("Error in database while getting user data.");

                returnStatement[2] = "Error";
                returnStatement[3] = "Error";
                returnStatement[4] = "Error";
                returnStatement[5] = "Error";
                returnStatement[6] = "Error";
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
                String insertUserSqlData = "INSERT INTO user_basic_data_table (entry_date, user_id) VALUES (?, ?)";
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
                preparedStatement = connection.prepareStatement(insertUserSqlData);
                preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
                preparedStatement.setInt(2, maxUserId + 1);
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
