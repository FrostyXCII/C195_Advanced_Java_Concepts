/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.Customer;
import Model.User;
import util.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author Kody McLeod
 */
public class AppointmentAddController implements Initializable {

    @FXML
    private Label AppointmentLabel;
    @FXML
    private TextField AppointmentTitleTextField;
    @FXML
    private TextField AppointmentDescriptionTextField;
    @FXML
    private DatePicker AppointmentDatePicker;
    @FXML
    private TextField AppointmentUrlTextField;
    @FXML
    private ComboBox<String> AppointmentTypeComboBox;
    @FXML
    private ComboBox<String> AppointmentContactComboBox;
    @FXML
    private ComboBox<String> AppointmentLocationComboBox;
    @FXML
    private ComboBox<String> AppointmentStartComboBox;
    @FXML
    private ComboBox<String> AppointmentEndComboBox;
    @FXML
    private Button AppointmentSaveButton;
    @FXML
    private TableView<Customer> AppointmentCustomerTable;
    @FXML
    private TableColumn<Customer, Integer> AppointmentCustomerTableCustomerIDColumn;
    @FXML
    private TableColumn<Customer, String> AppointmentCustomerTableCustomerNameColumn;
    @FXML
    private TextField AppointmentCustomerTextField;
    @FXML
    private Button AppointmentBackButton;

    Parent root;
    Stage stage;

    Customer selectedCustomer = new Customer();

    
    private Appointment selectedAppointment;
    private final ZoneId localZoneID = ZoneId.systemDefault(); //local zoneId

    private ObservableList<Customer> customerOL = FXCollections.observableArrayList();
    private final ObservableList<String> startTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofPattern("HH:mm:ss");//ISO standard time format
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ObservableList<Appointment> apptTimeList;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        PropertyValueFactory<Customer, Integer> customerIDFactory = new PropertyValueFactory<>("CustomerID");
        PropertyValueFactory<Customer, String> customerNameFactory = new PropertyValueFactory<>("CustomerName");

        AppointmentCustomerTableCustomerIDColumn.setCellValueFactory(customerIDFactory);
        AppointmentCustomerTableCustomerNameColumn.setCellValueFactory(customerNameFactory);
        AppointmentCustomerTextField.setEditable(false);
      
        try {
            updateCustomerTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Populates data into respective ComboBoxes.
        fillTypeList();
        fillContactList();
        fillLocationList();
        fillStartTimesList();

        //Lambda that listens for mouse click on item in Customer Table and populates customer text field with selected customer name.
        AppointmentCustomerTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showCustomerName(newValue);
        });
        
        
        //Lambda that disables weekends from being selected for appointments.
        AppointmentDatePicker.setDayCellFactory(dp -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    setDisable(empty || item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY );
                }
            };
        });
        
        //Populates appointment fields only if updating.
        if (AppointmentsMainController.boxtype == 2) {
            selectedAppointment = AppointmentsMainController.getUpdateAppointment();
            selectedCustomer.setCustomerID(selectedAppointment.getCustomerID());
            
            AppointmentCustomerTable.getSelectionModel().select(selectedCustomer);
            updateAppointmentFields();
        }
    }

    private void showCustomerName(Customer newValue) {
        AppointmentCustomerTextField.setText(newValue.getCustomerName());
        selectedCustomer = newValue;
    }

    //Fills appointment fields with data from selected appointment for update.
    private void updateAppointmentFields() {
        
        AppointmentLabel.setText("Update Appointment");
        
        selectedCustomer.setCustomerID(selectedAppointment.getCustomerID());

        //Gets local start time.
        String startLocal = selectedAppointment.getStart();

        //Gets local end time.
        String endLocal = selectedAppointment.getEnd();
       
        LocalDateTime localDateTimeStart = LocalDateTime.parse(startLocal, datetimeDTF);
        LocalDateTime localDateTimeEnd = LocalDateTime.parse(endLocal, datetimeDTF);
        
        LocalDate localDate = localDateTimeStart.toLocalDate();

        //Populates appointment fields with the selected appointment to be updated.
        AppointmentCustomerTextField.setText(selectedAppointment.getCustomerName());
        AppointmentTitleTextField.setText(selectedAppointment.getTitle());
        AppointmentDescriptionTextField.setText(selectedAppointment.getDescription());
        AppointmentTypeComboBox.setValue(selectedAppointment.getType());
        AppointmentContactComboBox.setValue(selectedAppointment.getContact());
        AppointmentLocationComboBox.setValue(selectedAppointment.getLocation());
        AppointmentDatePicker.setValue(localDate);
        AppointmentStartComboBox.getSelectionModel().select(localDateTimeStart.toLocalTime().format(timeDTF));
        AppointmentEndComboBox.getSelectionModel().select(localDateTimeEnd.toLocalTime().format(timeDTF));
        AppointmentUrlTextField.setText(selectedAppointment.getUrl());
    }

    //Updates customer table with any recent changes.
    public void updateCustomerTable() {
        
        try{
            //Creates statement object.
            PreparedStatement ps = ConnectDB.conn.prepareStatement("SELECT customerId, customerName FROM customer");

            //Executes statement and create resultset object.
            ResultSet result = ps.executeQuery();
       
            //Gets all records from resultset object.
            while (result.next()) {
            Customer cust = new Customer();
            cust.setCustomerName(result.getString("customerName"));
            cust.setCustomerID(result.getInt("customerId"));
            customerOL.addAll(cust);
            }
        
            AppointmentCustomerTable.setItems(customerOL);
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearAppointmentFields() {
        
        AppointmentCustomerTextField.setText("");
        AppointmentTitleTextField.setText("");
        AppointmentDescriptionTextField.setText("");
        AppointmentTypeComboBox.getSelectionModel().clearSelection();
        AppointmentContactComboBox.getSelectionModel().clearSelection();
        AppointmentLocationComboBox.getSelectionModel().clearSelection();
        AppointmentDatePicker.setValue(null);
        AppointmentStartComboBox.getSelectionModel().clearSelection();
        AppointmentEndComboBox.getSelectionModel().clearSelection();
        AppointmentUrlTextField.setText("");
        
    }

    private void fillStartTimesList() {
        
        //Limits Start and End times to hours between 9AM-5PM.
        //Appointment intervals are set at 30min increments.
        LocalTime time = LocalTime.of(9, 0, 0);
        do {
            startTimes.add(time.format(timeDTF));
            endTimes.add(time.format(timeDTF));
            time = time.plusMinutes(30);
        } while (!time.equals(LocalTime.of(17, 30, 0)));
        startTimes.remove(startTimes.size() - 1);
        endTimes.remove(0);
        if (AppointmentsMainController.boxtype != 2) {
            AppointmentDatePicker.setValue(LocalDate.now());
        }
        AppointmentStartComboBox.setItems(startTimes);
        AppointmentEndComboBox.setItems(endTimes);
        AppointmentStartComboBox.getSelectionModel().select(LocalTime.of(9, 0, 0).format(timeDTF));
        AppointmentEndComboBox.getSelectionModel().select(LocalTime.of(9, 30, 0).format(timeDTF));
    }

    private void fillContactList() {
        
        //Populates contact list with type of contact used to set appointment.
        ObservableList<String> contactList = FXCollections.observableArrayList();
        contactList.addAll("Email", "Phone", "In-person");
        AppointmentContactComboBox.setItems(contactList);
    }

    private void fillTypeList() {
        
        //Populates list with four categories of appointment types.
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll("New Account", "Consultation", "Follow-Up", "Close Account");
        AppointmentTypeComboBox.setItems(typeList);
    }

    private void fillLocationList() {
        
        //Populates list with all the available appointment locations.
        ObservableList<String> locationList = FXCollections.observableArrayList();
        locationList.addAll("Phoenix", "New York", "Tampa", "Orlando", "Dallas", "London", "Liverpool");
        AppointmentLocationComboBox.setItems(locationList);
    }

    private ObservableList<Customer> fillCustomerList() {
        
        //Populates customer table with all available customers.
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        int customerID;
        String customerName;
        
        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("SELECT customerId, CustomerName FROM customer");
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                customerID = result.getInt(selectedCustomer.getCustomerID());
                customerName = result.getString(selectedCustomer.getCustomerName());
                customerList.add(new Customer(customerID, customerName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return customerList;
    }

    private boolean validAppointment() {
        
        Customer customer = AppointmentCustomerTable.getSelectionModel().getSelectedItem();
        String title = AppointmentTitleTextField.getText();
        String description = AppointmentDescriptionTextField.getText();
        String type = AppointmentTypeComboBox.getValue();
        String contact = AppointmentContactComboBox.getValue();
        String location = AppointmentLocationComboBox.getValue();
        String url = AppointmentUrlTextField.getText();
        LocalDate localDate = AppointmentDatePicker.getValue();
        LocalTime startTime = LocalTime.parse(AppointmentStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime endTime = LocalTime.parse(AppointmentEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);

        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));

        String errorMessage = "";
        
        //First checks to see if inputs are null.
        if (customer == null) {
            errorMessage += "Please Select a Customer.\n";
        }
        if (title == null || title.length() == 0) {
            errorMessage += "You must enter an Appointment title.\n";
        }
        if (description == null || description.length() == 0) {
            errorMessage += "You must enter an appointment description.\n";
        }
        if (type == null || type.length() == 0) {
            errorMessage += "You must select an Appointment type.\n";
        }
        if (contact == null || contact.length() == 0) {
            errorMessage += "You must select an Appointment contact.\n";
        }
        if (location == null || location.length() == 0) {
            errorMessage += "You must select an Appointment location.\n";
        }
        if (url == null || url.length() == 0) {
            errorMessage += "You must enter an Appointment URL.\n";
        }
        if (startUTC == null) {
            errorMessage += "You must select a Start time";
        }
        if (endUTC == null) {
            errorMessage += "You must select an End time.\n";
            
        } else if (endUTC.equals(startUTC) || endUTC.isBefore(startUTC)) {  //Checks to make sure Start and End times are not the same.
            errorMessage += "End time must be after Start time.\n";
        } else {
            try {
                
                if (hasConflict(startUTC, endUTC)) { //Checks user's existing appointments for time conflicts.
                    errorMessage += "Appointment times conflict with Consultant's other appointments.\n";
                }
            } catch (Exception e) {
                Logger.getLogger(AppointmentAddController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            
            //Shows the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Invalid Appointment");
            alert.setContentText(errorMessage);
            Optional<ButtonType> result = alert.showAndWait();
            
            return false;
        }
    }
    
    //Checks to make sure current appointment being set doesnt conflict with consultant's other appointments.
    private boolean hasConflict(ZonedDateTime newStart, ZonedDateTime newEnd) {
        int appointmentID = -1;
        String consultant;
        
        if (AppointmentsMainController.boxtype == 2) {
            
            //Edited appointment.
            appointmentID = selectedAppointment.getAppointmentID();
            consultant = selectedAppointment.getCreatedBy();
        } else {
            
            //New appointment.            
            consultant = User.getUsername();
        }

        try {

            PreparedStatement pst = ConnectDB.makeConnection().prepareStatement(
                    "SELECT * FROM appointment "
                    + "WHERE (? BETWEEN start AND end OR ? BETWEEN start AND end OR ? < start AND ? > end) "
                    + "AND (createdBy = ? AND appointmentID != ?)");
            pst.setTimestamp(1, Timestamp.valueOf(newStart.toLocalDateTime()));
            pst.setTimestamp(2, Timestamp.valueOf(newEnd.toLocalDateTime()));
            pst.setTimestamp(3, Timestamp.valueOf(newStart.toLocalDateTime()));
            pst.setTimestamp(4, Timestamp.valueOf(newEnd.toLocalDateTime()));
            pst.setString(5, consultant);
            pst.setInt(6, appointmentID);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    //Saves new appointments.
    private void saveAppointment() {
        
        //Captures dates and time in local time.
        LocalDate localDate = AppointmentDatePicker.getValue(); //Returns date value without time.
        LocalTime localStartTime = LocalTime.parse(AppointmentStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime localEndTime = LocalTime.parse(AppointmentEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);

        //Combines date and start/end times together.       
        LocalDateTime startDT = LocalDateTime.of(localDate, localStartTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, localEndTime);

        //Converts startDT and endDT to UTC.
        ZonedDateTime startUTC = startDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        
        //Converts UTC time to a timestamp for database insertion.
        Timestamp sqlStartTS = Timestamp.valueOf(startUTC.toLocalDateTime());
        Timestamp sqlEndTS = Timestamp.valueOf(endUTC.toLocalDateTime());
        
        try {
            
            int newAppointmentID = -1;
            
            //Inserts new address into DB.
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
            selectedCustomer.setCustomerID(AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
            ps.setInt(2, User.getUserID());
            ps.setString(3, AppointmentTitleTextField.getText());
            ps.setString(4, AppointmentDescriptionTextField.getText());
            ps.setString(5, AppointmentLocationComboBox.getSelectionModel().getSelectedItem());
            ps.setString(6, AppointmentContactComboBox.getSelectionModel().getSelectedItem());
            ps.setString(7, AppointmentTypeComboBox.getSelectionModel().getSelectedItem());
            ps.setString(8, AppointmentUrlTextField.getText());
            ps.setTimestamp(9, sqlStartTS);
            ps.setTimestamp(10, sqlEndTS);
            ps.setString(11, User.getUsername());
            ps.setString(12, User.getUsername());
            
            int result = ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                newAppointmentID = rs.getInt(1);
            }
            
            clearAppointmentFields();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Updates existing appointments with any changes.
    private void updateAppointment() {
        
        //Captures dates and time in local time.
        LocalDate localDate = AppointmentDatePicker.getValue(); //Returns date value without time.
        LocalTime localStartTime = LocalTime.parse(AppointmentStartComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        LocalTime localEndTime = LocalTime.parse(AppointmentEndComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        
        //Combines date and start/end times together.       
        LocalDateTime startDT = LocalDateTime.of(localDate, localStartTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, localEndTime);
        
        //Converts startDT and endDT to UTC.
        ZonedDateTime startUTC = startDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(localZoneID).withZoneSameInstant(ZoneId.of("UTC"));
        
        //Converts UTC time to a timestamp for database insertion.
        Timestamp sqlStartTS = Timestamp.valueOf(startUTC.toLocalDateTime());
        Timestamp sqlEndTS = Timestamp.valueOf(endUTC.toLocalDateTime());
        
        try {
            
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("UPDATE appointment "
                    + "SET customerId = ?, userId = ?, title = ?, description = ?, "
                    + "location = ?, contact = ?, type = ?, url = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                    + "WHERE appointmentId = ?");
            
            if (selectedCustomer.getCustomerID() <= 0) {
                selectedCustomer.setCustomerID(AppointmentCustomerTable.getSelectionModel().getSelectedItem().getCustomerID());
            }
            
            ps.setInt(1, selectedCustomer.getCustomerID());
            ps.setInt(2, User.getUserID());
            ps.setString(3, AppointmentTitleTextField.getText());
            ps.setString(4, AppointmentDescriptionTextField.getText());
            ps.setString(5, AppointmentLocationComboBox.getSelectionModel().getSelectedItem());
            ps.setString(6, AppointmentContactComboBox.getSelectionModel().getSelectedItem());
            ps.setString(7, AppointmentTypeComboBox.getSelectionModel().getSelectedItem());
            ps.setString(8, AppointmentUrlTextField.getText());
            ps.setTimestamp(9, sqlStartTS);
            ps.setTimestamp(10, sqlEndTS);
            ps.setString(11, User.getUsername());
            ps.setInt(12, selectedAppointment.getAppointmentID());
            
            int result = ps.executeUpdate();
            
            clearAppointmentFields();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Cancel Button Handler.
    @FXML
    private void AppointmentCancelButtonHandler(MouseEvent event) {
        try{
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Required");
            alert.setHeaderText("Confirm Cancel");
            alert.setContentText("Are you sure you want to cancel?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
            
                root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
                stage = (Stage) AppointmentSaveButton.getScene().getWindow();
                Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            
            } 
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Save Button Handler.
    @FXML
    private void AppointmentSaveButtonHandler(MouseEvent event) {
        try {
            
            if (validAppointment()) {
                
                if (AppointmentsMainController.boxtype == 1) {
                    saveAppointment();
                    AppointmentLabel.setText("Add Appointment");
                    root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
                    stage = (Stage) AppointmentBackButton.getScene().getWindow();
                    Scene scene = new Scene(root);

                    stage.setScene(scene);
                    stage.show();
                    
                } else if (AppointmentsMainController.boxtype == 2) {
                    updateAppointment();
                    AppointmentLabel.setText("Add Appointment");
                    root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
                    stage = (Stage) AppointmentBackButton.getScene().getWindow();
                    Scene scene = new Scene(root);

                    stage.setScene(scene);
                    stage.show();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //Back Button Handler.
    @FXML
    private void AppointmentBackButtonHandler(MouseEvent event) {
        try{
            
            root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
            stage = (Stage) AppointmentBackButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
