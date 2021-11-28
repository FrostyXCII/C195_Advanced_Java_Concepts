/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.User;
import util.ConnectDB;
import util.DateTime;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kody McLeod
 */
public class LoginController implements Initializable {

    @FXML
    private TextField UsernameTextField;
    @FXML
    private Label LoginUsernameLabel;
    @FXML
    private PasswordField PasswordTextField;
    @FXML
    private Label LoginPasswordLabel;
    @FXML
    private Button LoginButton;
    @FXML
    private Label LoginLabel;
    
    ObservableList<Appointment> appointmentReminderOL = FXCollections.observableArrayList();
    private DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private ZoneId localZoneId = ZoneId.systemDefault();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
            
            rb = ResourceBundle.getBundle("language_files/rb", Locale.getDefault());
            System.out.println(Locale.getDefault());
            LoginLabel.setText(rb.getString("title"));
            LoginUsernameLabel.setText(rb.getString("username"));
            LoginPasswordLabel.setText(rb.getString("password"));
            LoginButton.setText(rb.getString("signin"));
            
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
    }

    //Filters the reminder list and alerts if appointment is within 15 minutes.
    private void appointmentAlert() {
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus15Min = now.plusMinutes(15);
        
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentReminderOL);

        //Lambda expression used to efficiently identify any appointment starting within the next 15 minutes.
        filteredData.setPredicate(row -> {
            LocalDateTime rowDate = LocalDateTime.parse(row.getStart().substring(0, 16), datetimeDTF);
            return rowDate.isAfter(now.minusMinutes(1)) && rowDate.isBefore(nowPlus15Min);
        }
        );
        if (filteredData.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment Reminder");
            alert.setHeaderText("No upcoming appointment alerts.");
            alert.showAndWait();
            
        } else {
            String type = filteredData.get(0).getDescription();
            String customer = filteredData.get(0).getCustomerName();
            String start = filteredData.get(0).getStart().substring(0, 16);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment Reminder");
            alert.setHeaderText("Reminder - You have an appointment scheduled within the next 15 minutes.");
            alert.setContentText("Your upcoming appointment with " + customer
                    + " is currently set to begin at " + start + ".");
            alert.showAndWait();
        }
    }

    //Creates reminder list to be checked with the appointment alert.
    private void createReminderList() {

        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, "
                    + "appointment.`start`, appointment.`end`, customer.customerId, customer.customerName, appointment.createdBy "
                    + "FROM appointment, customer "
                    + "WHERE appointment.customerId = customer.customerId AND appointment.createdBy = ? "
                    + "ORDER BY `start`");
            ps.setString(1, User.getUsername());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {                           

                //Pulls start time from database and converts it into local time zone.
                Timestamp timestampStart = rs.getTimestamp("start");
                ZonedDateTime startUTC = timestampStart.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalStart = startUTC.withZoneSameInstant(localZoneId);

                //Pulls end time from database and converts it into local time zone.
                Timestamp timestampEnd = rs.getTimestamp("end");
                ZonedDateTime endUTC = timestampEnd.toLocalDateTime().atZone(ZoneId.of("UTC"));
                ZonedDateTime newLocalEnd = endUTC.withZoneSameInstant(localZoneId);

                //Pulls select data fields for use in appointmentReminderOL observablelist.
                int appointmentId = rs.getInt("appointmentId");   
                String title = rs.getString("title");
                String type = rs.getString("description");
                String customerName = rs.getString("customerName");
                int customerId = rs.getInt("customerId");
                String user = rs.getString("createdBy");

                //Inserts Appointment objects into observablelist.
                appointmentReminderOL.add(new Appointment(appointmentId, newLocalStart.toString(), newLocalEnd.toString(), title, type, customerId, customerName, user));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void LoginButtonHandler(MouseEvent event) {
        
        ResourceBundle rb = ResourceBundle.getBundle("language_files/rb", Locale.getDefault());
        
        try {
            String usernameInput = UsernameTextField.getText();
            String passwordInput = PasswordTextField.getText();
            int userID = getUserID(usernameInput);
            Parent root;
            Stage stage;
            User user = new User();

            if (isValidPassword(userID, passwordInput)) {
                user.setUserID(userID);
                user.setUsername(usernameInput);

            
                //Calls method to write current user to the log.
                loginLog(user.getUsername());
                createReminderList();
                appointmentAlert();

                //Calls mainscreen scene after successful login.
                root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                stage = (Stage) LoginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } else {
                
                ButtonType OK = new ButtonType(rb.getString("ok"), ButtonData.OK_DONE);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION, rb.getString("incorrect"), OK);
                alert.setTitle("");
                alert.setHeaderText(rb.getString("incorrect2"));
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Creates a new log file if one doesnt exist and inserts login information for current user.
    public void loginLog(String user) {
        try {
            String fileName = "loginLog.txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(DateTime.getTimeStamp() + " " + user + " " + "\n");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidPassword(int userID, String password) {
        try {
            //Create statement object.
            Statement statement = ConnectDB.conn.createStatement();

            //Write SQL statement.
            String sqlStatement = "SELECT password FROM user WHERE userId ='" + userID + "'";;

            //Create resultset object.
            ResultSet result = statement.executeQuery(sqlStatement);

            while (result.next()) {
                if (result.getString("password").equals(password)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    //Gets User ID for current user.
    private int getUserID(String username) {
        
        int userID = -1;
        
        try {

            //Create statement object.
            Statement statement = ConnectDB.conn.createStatement();

            //Write SQL statement.
            String sqlStatement = "SELECT userID FROM user WHERE userName ='" + username + "'";

            //Create resultset object.
            ResultSet result = statement.executeQuery(sqlStatement);

            while (result.next()) {
                userID = result.getInt("userId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return userID;
    }
}
