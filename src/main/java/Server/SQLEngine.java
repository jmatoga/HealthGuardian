package Server;

import Client.Client;
import Client.Start;
import ScenesControllers.SceneSwitch;
import com.healthguardian.WindowApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import utils.Color;

import java.net.SocketException;
import java.sql.*;

import static Client.Client.clientId;

public class SQLEngine {
    private final String url;
    private final String username;
    private final String password;

    public SQLEngine(String host, int port, String database, String username, String password) {
        // old (local) database //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthguardian", "root", "root");
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        this.username = username;
        this.password = password;
    }

    public Connection connectToDataBase(Connection connection, int clientId) {
        try {
            connection = DriverManager.getConnection(url, username, password);
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
        String[] returnStatement = {"Error", "Error", "Error", "Error", "Error", "Error", "Error"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT first_Name, last_name, user_data.age, user_data.weight, user_data.height, user_data.systolic_pressure, user_data.diastolic_pressure FROM user NATURAL JOIN user_data WHERE user_id = ?;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user_id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Getted User Data.");

                returnStatement[0] = String.valueOf(resultSet.getString("first_name"));
                returnStatement[1] = String.valueOf(resultSet.getString("last_name"));
                returnStatement[2] = String.valueOf(resultSet.getString("user_data.age"));
                returnStatement[3] = String.valueOf(resultSet.getString("user_data.weight"));
                returnStatement[4] = String.valueOf(resultSet.getString("user_data.height"));
                returnStatement[5] = String.valueOf(resultSet.getString("user_data.systolic_pressure"));
                returnStatement[6] = String.valueOf(resultSet.getString("user_data.diastolic_pressure"));

                return returnStatement;

            } else {
                System.out.println("DataBase Error, First Name is empty!.");

                returnStatement[0] = "false1";

                return returnStatement;
            }
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
            String sql = "SELECT username FROM user_pass WHERE username = ?";
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

    boolean checkOneTimeCode(int clientID, String oneTimeCode){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            String sql = "SELECT code FROM one_time_code WHERE code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, oneTimeCode);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if code exist in database
                System.out.println("Correct code!");
                return true;
            } else {
                System.out.println("Wrong code!");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error while executing SELECT: " + e.getMessage());
        } finally {
            disconnectFromDataBase(resultSet, preparedStatement, connection);
        }
        return true;
    }

    String[] loginToAccount(int clientID, String inputUsername, String inputPassword) {
        String[] returnStatement = {"false", "-1"};

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = connectToDataBase(connection, clientID);
            System.out.println(connection);
            String sql = "SELECT username,user_id FROM user_pass WHERE username = ? AND password_hash = SHA2(CONCAT(?, (SELECT salt FROM user_pass WHERE username=?)), 256)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, inputUsername);  // username
            preparedStatement.setString(2, inputPassword);  // password
            preparedStatement.setString(3, inputUsername);  // username

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) { // if username and password exist in database in same user
                System.out.println("Correct password.\n");

                returnStatement[0] = "true";
                returnStatement[1] = String.valueOf(resultSet.getInt("user_id"));

                return returnStatement;
            } else {
                System.out.println("Wrong password.\n");

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
