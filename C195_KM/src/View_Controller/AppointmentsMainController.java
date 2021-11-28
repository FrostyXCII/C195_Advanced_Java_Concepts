/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.Customer;
import util.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kody McLeod
 */
public class AppointmentsMainController implements Initializable {

    @FXML
    private Button AddNewAppointmentButton;
    @FXML
    private Button UpdateAppointmentButton;
    @FXML
    private TableView<Appointment> AppointmentTableView;
    @FXML
    private TableColumn<Appointment, String> AppointmentStartColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentEndColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentTypeColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentCustomerColumn;
    @FXML
    private TableColumn<Appointment, String> AppointmentConsultantColumn;
    @FXML
    private RadioButton AppointmentWeekRadioButton;
    @FXML
    private RadioButton AppointmentMonthRadioButton;
    
    private ToggleGroup RadioButtonToggleGroup;
    private boolean isWeekly;
    private static Appointment updateAppointment;
    private static int updateAppointmentIndex;
    public static int boxtype; //Used to identify Add/Update label on AppointmentAddController.
    private Customer customer = new Customer();
    private Appointment appointment = new Appointment();

    Parent root;
    Stage stage;

    ObservableList<Appointment> appointmentsOL = FXCollections.observableArrayList();
    private static Appointment selectedAppointment = new Appointment();
    
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ZoneId localZoneID = ZoneId.systemDefault();
    private final ZoneId utcZoneID = ZoneId.of("UTC");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Populates CustomerTable with values.        
        PropertyValueFactory<Appointment, String> apptStartFactory = new PropertyValueFactory<>("Start");
        PropertyValueFactory<Appointment, String> apptEndFactory = new PropertyValueFactory<>("End");
        PropertyValueFactory<Appointment, String> apptTitleFactory = new PropertyValueFactory<>("Title");
        PropertyValueFactory<Appointment, String> apptTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<Appointment, String> apptCustomerFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Appointment, String> apptConsultantFactory = new PropertyValueFactory<>("CreatedBy");

        AppointmentStartColumn.setCellValueFactory(apptStartFactory);
        AppointmentEndColumn.setCellValueFactory(apptEndFactory);
        AppointmentTitleColumn.setCellValueFactory(apptTitleFactory);
        AppointmentTypeColumn.setCellValueFactory(apptTypeFactory);
        AppointmentCustomerColumn.setCellValueFactory(apptCustomerFactory);
        AppointmentConsultantColumn.setCellValueFactory(apptConsultantFactory);

        //Sets the togglegroup.
        RadioButtonToggleGroup = new ToggleGroup();
        AppointmentWeekRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentMonthRadioButton.setToggleGroup(RadioButtonToggleGroup);
        AppointmentWeekRadioButton.setSelected(true);
        AppointmentMonthRadioButton.setSelected(false);

        isWeekly = true;

        try {
            setAppointmentsTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Method that returns updateAppointment object that was selected for update.
    public static Appointment getUpdateAppointment() {
        return updateAppointment;
    }

    public static int getUpdateAppointmentIndex() {
        return updateAppointmentIndex;
    }

    //Populates table view with appointments and applies filter.
    public void setAppointmentsTable() {
        
        PreparedStatement ps;
        try {
            
            ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT appointment.appointmentId, appointment.customerId, appointment.userId, appointment.title, appointment.description, "
                    + "appointment.location, appointment.contact, appointment.type, appointment.url, appointment.start, appointment.end, "
                    + "appointment.createdBy, customer.customerId, customer.customerName "
                    + "FROM appointment, customer "
                    + "WHERE appointment.customerId = customer.customerId "
                    + "ORDER BY `start`");

            ResultSet rs = ps.executeQuery();
            
            appointmentsOL.clear();

            while (rs.next()) {

                //Assigns variables with data from database for insertion into appointments observableList.
                int appointmentID = rs.getInt("appointmentId");
                int customerID = rs.getInt("customerId");
                int userID = rs.getInt("userId");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String url = rs.getString("url");

                //Gets database start time stored as UTC.
                String startUTC = rs.getString("start").substring(0, 19);

                //Gets database end time stored as UTC.
                String endUTC = rs.getString("end").substring(0, 19);

                //Converts database UTC to LocalDateTime.
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //Converts times UTC zoneId to local zoneId.
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);

                //Converts ZonedDateTime to a string for insertion into AppointmentsTableView.
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);

                //Gets title from appointment.
                String title = rs.getString("title");

                //Gets type from appointment.
                String type = rs.getString("type");

                //Puts Customer data into Customer object.
                Customer customer = new Customer(rs.getInt("customerId"), rs.getString("customerName"));
                String customerName = customer.getCustomerName();

                String user = rs.getString("createdBy");

                //Inserts appointments into observablelist for AppointmentTableView if userName = createdBy.
                appointmentsOL.add(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, url, localStartDT, localEndDT, customerName, user));
            }

            //Filters appointments by week or month.
            if (isWeekly) {
                filterAppointmentsByWeek(appointmentsOL);
            } else {
                filterAppointmentsByMonth(appointmentsOL);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void AddNewAppointmentButtonHandler(MouseEvent event) {
        try {
            
            boxtype = 1;
            root = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
            stage = (Stage) AddNewAppointmentButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void UpdateAppointmentButtonHandler(MouseEvent event) {
        //Checks that an appointment has been selected.
        try{
            if (AppointmentTableView.getSelectionModel().getSelectedItem() != null) {
                
                updateAppointment = AppointmentTableView.getSelectionModel().getSelectedItem();
                //System.out.println("AppointmentID: " + updateAppointment.getAppointmentID());
                updateAppointmentIndex = appointmentsOL.indexOf(updateAppointment);
                boxtype = 2;

                //Gets reference to the button's stage.
                root = FXMLLoader.load(getClass().getResource("AppointmentAdd.fxml"));
                stage = (Stage) UpdateAppointmentButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                
            } else {
                
                //Shows the error message.
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Select Appointment!");
                alert.setContentText("No appointment has been selected to modify.");
                Optional<ButtonType> result = alert.showAndWait();                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteAppointment(Appointment appointment) {
        
        Appointment appt = appointment;
        
        try {
            
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("DELETE appointment.* FROM appointment WHERE appointment.appointmentId = ? ");       
            ps.setInt(1, appt.getAppointmentID());
            int result = ps.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setAppointmentsTable();
    }

    @FXML
    private void DeleteAppointmentButtonHandler(MouseEvent event) {
        try {
            if (AppointmentTableView.getSelectionModel().getSelectedItem() != null) {

                Appointment appt = AppointmentTableView.getSelectionModel().getSelectedItem();
                int appointmentID = appt.getAppointmentID();
                System.out.println("AppointmentID : " + appointmentID);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Required");
                alert.setHeaderText("Confirm DELETE");
                alert.setContentText("Are you sure you want to DELETE this appointment?");
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {
                
                    deleteAppointment(appt);         
                    setAppointmentsTable();
                }
            
            } else {
                //Shows the error message.
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Select Appointment!");
                alert.setContentText("No appointment has been selected to delete.");
                Optional<ButtonType> result = alert.showAndWait();        
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void AppointmentWeekRadioButtonHandler(MouseEvent event) {
            isWeekly = true;
            setAppointmentsTable();
    }

    @FXML
    private void AppointmentMonthRadioButtonHandler(MouseEvent event) {
        isWeekly = false;
        setAppointmentsTable();
    }

    public void filterAppointmentsByMonth(ObservableList appointmentsOL) {

        //Filters appointments for month.
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Month = now.plusMonths(1);

        //Lambda expression used to efficiently filter appointments by month.
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentsOL);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);
            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
        });

        AppointmentTableView.setItems(filteredData);
    }

    public void filterAppointmentsByWeek(ObservableList appointmentsOL) {
        
        //Filter appointments for week.
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Week = now.plusWeeks(1);

        //Lambda expression used to efficiently filter appointments by week.
        FilteredList<Appointment> filteredData = new FilteredList<>(appointmentsOL);
        filteredData.setPredicate(row -> {
            
            LocalDate rowDate = LocalDate.parse(row.getStart(), datetimeDTF);
            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Week);
        });
        AppointmentTableView.setItems(filteredData);
    }

    @FXML
    private void AppointmentsBackButtonHandler(MouseEvent event) {
        try {
            
            root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            stage = (Stage) UpdateAppointmentButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
