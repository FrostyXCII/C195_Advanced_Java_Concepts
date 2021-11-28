/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.Customer;
import Model.Reports;
import Model.User;
import util.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Kody McLeod
 */
public class ReportsController implements Initializable {

    @FXML
    private TableView<Reports> ReportsByMonthTableView;
    @FXML
    private TableColumn<Reports, String> ReportsByMonthMonthColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthNewAccountColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthConsultationColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthFollowUpColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsByMonthCloseAccountColumn;
    @FXML
    private TableView<Appointment> ReportsScheduleTableView;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleCustomerColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleTitleColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleTypeColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleStartColumn;
    @FXML
    private TableColumn<Appointment, String> ReportsScheduleEndColumn;
    @FXML
    private TableView<Reports> ReportsContactsByMonthTableView;
    @FXML
    private TableColumn<Reports, String> ReportsContactsMonthColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsContactEmailColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsContactPhoneColumn;
    @FXML
    private TableColumn<Reports, Integer> ReportsContactInpersonColumn;

    @FXML
    private Button ReportsMainMenuButton;

    Parent root;
    Stage stage;

    //Creates observableLists used for the 3 available reports.
    private ObservableList<Appointment> scheduleOL = FXCollections.observableArrayList();
    private ObservableList<Reports> typesByMonthOL = FXCollections.observableArrayList();
    private ObservableList<Reports> contactsByMonthOL = FXCollections.observableArrayList();
    private final DateTimeFormatter datetimeDTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final ZoneId localZoneID = ZoneId.systemDefault();
    private final ZoneId utcZoneID = ZoneId.of("UTC");

    //Create array for storing how many of each contacts are in each month.
    private int monthContacts[][] = new int[][]{
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0},
        {0, 0, 0}
    };
    
    //Create array for storing how many of each appointment types are in each month.
    private int monthTypes[][] = new int[][]{
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0},
        {0, 0, 0, 0}
    };
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Populate consultant schedule.        
        PropertyValueFactory<Appointment, String> apptStartFactory = new PropertyValueFactory<>("Start");
        PropertyValueFactory<Appointment, String> apptEndFactory = new PropertyValueFactory<>("End");
        PropertyValueFactory<Appointment, String> apptTitleFactory = new PropertyValueFactory<>("Title");
        PropertyValueFactory<Appointment, String> apptTypeFactory = new PropertyValueFactory<>("Type");
        PropertyValueFactory<Appointment, String> apptCustomerFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Appointment, String> apptConsultantFactory = new PropertyValueFactory<>("CreatedBy");

        //Assign cell values to schedule report.   
        ReportsScheduleStartColumn.setCellValueFactory(apptStartFactory);
        ReportsScheduleEndColumn.setCellValueFactory(apptEndFactory);
        ReportsScheduleCustomerColumn.setCellValueFactory(apptCustomerFactory);
        ReportsScheduleTitleColumn.setCellValueFactory(apptTitleFactory);
        ReportsScheduleTypeColumn.setCellValueFactory(apptTypeFactory);

        //Populate types by month.
        PropertyValueFactory<Reports, String> monthFactory = new PropertyValueFactory<>("Month");
        PropertyValueFactory<Reports, Integer> newFactory = new PropertyValueFactory<>("NewAccount");
        PropertyValueFactory<Reports, Integer> consultationFactory = new PropertyValueFactory<>("Consultation");
        PropertyValueFactory<Reports, Integer> followUpFactory = new PropertyValueFactory<>("FollowUp");
        PropertyValueFactory<Reports, Integer> closeFactory = new PropertyValueFactory<>("CloseAccount");

        //Assign cell values to types by month.
        ReportsByMonthMonthColumn.setCellValueFactory(monthFactory);
        ReportsByMonthNewAccountColumn.setCellValueFactory(newFactory);
        ReportsByMonthConsultationColumn.setCellValueFactory(consultationFactory);
        ReportsByMonthFollowUpColumn.setCellValueFactory(followUpFactory);
        ReportsByMonthCloseAccountColumn.setCellValueFactory(closeFactory);

        //Populate contacts by month.
        PropertyValueFactory<Reports, String> monthContactFactory = new PropertyValueFactory<>("Month");
        PropertyValueFactory<Reports, Integer> emailFactory = new PropertyValueFactory<>("Email");
        PropertyValueFactory<Reports, Integer> phoneFactory = new PropertyValueFactory<>("Phone");
        PropertyValueFactory<Reports, Integer> inpersonFactory = new PropertyValueFactory<>("Inperson");
        
        //Assign cell values to contacts by month.
        ReportsContactsMonthColumn.setCellValueFactory(monthContactFactory);
        ReportsContactEmailColumn.setCellValueFactory(emailFactory);
        ReportsContactPhoneColumn.setCellValueFactory(phoneFactory);
        ReportsContactInpersonColumn.setCellValueFactory(inpersonFactory);
        
        setReportsScheduleTable();
        try {
            setReportsTypeByMonthTable();
        } catch (Exception e) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            setReportsContactsByMonthTable();
        } catch (Exception e) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, e);
        }
        ReportsScheduleTableView.setItems(scheduleOL);
        ReportsByMonthTableView.setItems(typesByMonthOL);
        ReportsContactsByMonthTableView.setItems(contactsByMonthOL);

    }

    @FXML
    private void ReportsMainMenuButtonHandler(MouseEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            stage = (Stage) ReportsMainMenuButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setReportsTypeByMonthTable() {
        
        PreparedStatement ps;
        
        try {
            
            ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT * "
                    + "FROM appointment");

            ResultSet rs = ps.executeQuery();
            
            typesByMonthOL.clear();
            
            while (rs.next()) {
                
                //Get database start time stored as UTC.
                String startUTC = rs.getString("start").substring(0, 19);

                //Get database end time stored as UTC.
                String endUTC = rs.getString("end").substring(0, 19);

                //Convert database UTC to LocalDateTime.
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //Convert times UTC zoneId to local zoneId.
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);

                //Convert ZonedDateTime to a string for insertion into AppointmentsTableView.
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);

                String monthParse = localStartDT.substring(5, 7);
                int month = Integer.parseInt(monthParse);
                
                month = month - 1;
                String type = rs.getString("type");

                //Switch to increment array values of each type for each month.
                switch (month) {
                    case 0:
                        if (type.equals("New Account")) {
                            monthTypes[0][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[0][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[0][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[0][3]++;
                        }   break;
                    case 1:
                        if (type.equals("New Account")) {
                            monthTypes[1][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[1][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[1][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[1][3]++;
                        }   break;
                    case 2:
                        if (type.equals("New Account")) {
                            monthTypes[2][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[2][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[2][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[2][3]++;
                        }   break;
                    case 3:
                        if (type.equals("New Account")) {
                            monthTypes[3][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[3][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[3][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[3][3]++;
                        }   break;
                    case 4:
                        if (type.equals("New Account")) {
                            monthTypes[4][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[4][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[4][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[4][3]++;
                        }   break;
                    case 5:
                        if (type.equals("New Account")) {
                            monthTypes[5][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[5][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[5][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[5][3]++;
                        }   break;
                    case 6:
                        if (type.equals("New Account")) {
                            monthTypes[6][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[6][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[6][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[6][3]++;
                        }   break;
                    case 7:
                        if (type.equals("New Account")) {
                            monthTypes[7][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[7][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[7][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[7][3]++;
                        }   break;
                    case 8:
                        if (type.equals("New Account")) {
                            monthTypes[8][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[8][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[8][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[8][3]++;
                        }   break;
                    case 9:
                        if (type.equals("New Account")) {
                            monthTypes[9][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[9][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[9][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[9][3]++;
                        }   break;
                    case 10:
                        if (type.equals("New Account")) {
                            monthTypes[10][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[10][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[10][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[10][3]++;
                        }   break;
                    case 11:
                        if (type.equals("New Account")) {
                            monthTypes[11][0]++;
                        } else if (type.equals("Consultation")) {
                            monthTypes[11][1]++;
                        } else if (type.equals("Follow-Up")) {
                            monthTypes[11][2]++;
                        } else if (type.equals("Close Account")) {
                            monthTypes[11][3]++;
                        }   break;
                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 12; i++) {
            //Assign variables for insertion into typesByMonthOL.
            int newAccount = monthTypes[i][0];
            int consultation = monthTypes[i][1];
            int followUp = monthTypes[i][2];
            int closeAccount = monthTypes[i][3];
            
            typesByMonthOL.add(new Reports(getAbbreviatedMonth(i), newAccount, consultation, followUp, closeAccount));
            
        }
    }
    
    private void setReportsContactsByMonthTable() {
        
        PreparedStatement ps;
        try {
            ps = ConnectDB.makeConnection().prepareStatement(
                    "SELECT * "
                    + "FROM appointment");

            ResultSet rs = ps.executeQuery();
            
            contactsByMonthOL.clear();            
            while (rs.next()) {
                
                //Get database start time stored as UTC.
                String startUTC = rs.getString("start").substring(0, 19);

                //Get database end time stored as UTC.
                String endUTC = rs.getString("end").substring(0, 19);

                //Convert database UTC to LocalDateTime.
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //Convert times UTC zoneId to local zoneId.
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);

                //Convert ZonedDateTime to a string for insertion into AppointmentsTableView.
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);
                                
                //Parse two digit value for month.
                String monthParse = localStartDT.substring(5, 7);
                int month = Integer.parseInt(monthParse);               
                month = month - 1;
                String contact = rs.getString("contact");

                //Switch to increment array values of each type for each month.
                switch (month) {
                    case 0:
                        if (contact.equals("Email")) {
                            monthContacts[0][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[0][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[0][2]++;
                        }   break;
                    case 1:
                        if (contact.equals("Email")) {
                            monthContacts[1][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[1][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[1][2]++;
                        }   break;
                    case 2:
                        if (contact.equals("Email")) {
                            monthContacts[2][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[2][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[2][2]++;
                        }   break;
                    case 3:
                        if (contact.equals("Email")) {
                            monthContacts[3][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[3][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[3][2]++;
                        }   break;
                    case 4:
                        if (contact.equals("Email")) {
                            monthContacts[4][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[4][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[4][2]++;
                        }   break;
                    case 5:
                        if (contact.equals("Email")) {
                            monthContacts[5][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[5][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[5][2]++;
                        }   break;
                    case 6:
                        if (contact.equals("Email")) {
                            monthContacts[6][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[6][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[6][2]++;
                        }   break;
                    case 7:
                        if (contact.equals("Email")) {
                            monthContacts[7][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[7][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[7][2]++;
                        }   break;
                    case 8:
                        if (contact.equals("Email")) {
                            monthContacts[7][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[7][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[7][2]++;
                        }   break;
                    case 9:
                        if (contact.equals("Email")) {
                            monthContacts[7][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[7][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[7][2]++;
                        }   break;
                    case 10:
                        if (contact.equals("Email")) {
                            monthContacts[10][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[10][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[10][2]++;
                        }   break;
                    case 11:
                        if (contact.equals("Email")) {
                            monthContacts[11][0]++;
                        } else if (contact.equals("Phone")) {
                            monthContacts[11][1]++;
                        } else if (contact.equals("In-person")) {
                            monthContacts[11][2]++;
                        }   break; 
                    default:
                        break;
                }
            }            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 12; i++) {
            //Variables to insert into observableList.
            int email = monthTypes[i][0];
            int phone = monthTypes[i][1];
            int inperson = monthTypes[i][2];
            
            contactsByMonthOL.add(new Reports(getAbbreviatedMonth(i), email, phone, inperson));            
        }
    }

    //Converts two digit month code into abbreviated month string.
    private String getAbbreviatedMonth(int month) {
        String abbreviatedMonth = null;
        if (month == 0) {
            abbreviatedMonth = "JAN";
        }
        if (month == 1) {
            abbreviatedMonth = "FEB";
        }
        if (month == 2) {
            abbreviatedMonth = "MAR";
        }
        if (month == 3) {
            abbreviatedMonth = "APR";
        }
        if (month == 4) {
            abbreviatedMonth = "MAY";
        }
        if (month == 5) {
            abbreviatedMonth = "JUN";
        }
        if (month == 6) {
            abbreviatedMonth = "JUL";
        }
        if (month == 7) {
            abbreviatedMonth = "AUG";
        }
        if (month == 8) {
            abbreviatedMonth = "SEP";
        }
        if (month == 9) {
            abbreviatedMonth = "OCT";
        }
        if (month == 10) {
            abbreviatedMonth = "NOV";
        }
        if (month == 11) {
            abbreviatedMonth = "DEC";
        }
        return abbreviatedMonth;
    }

    //Shows current consultant's schedule.
    private void setReportsScheduleTable() {
        
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
            
            scheduleOL.clear();

            while (rs.next()) {

                int appointmentID = rs.getInt("appointmentId");                
                int customerID = rs.getInt("customerId");                
                int userID = rs.getInt("userId");                
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String url = rs.getString("url");

                //Get database start time stored as UTC.
                String startUTC = rs.getString("start").substring(0, 19);               

                //Get database end time stored as UTC.
                String endUTC = rs.getString("end").substring(0, 19);               

                //Convert database UTC to LocalDateTime.
                LocalDateTime utcStartDT = LocalDateTime.parse(startUTC, datetimeDTF);
                LocalDateTime utcEndDT = LocalDateTime.parse(endUTC, datetimeDTF);

                //Convert times UTC zoneId to local zoneId.
                ZonedDateTime localZoneStart = utcStartDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
                ZonedDateTime localZoneEnd = utcEndDT.atZone(utcZoneID).withZoneSameInstant(localZoneID);
               

                //Convert ZonedDateTime to a string for insertion into AppointmentsTableView.
                String localStartDT = localZoneStart.format(datetimeDTF);
                String localEndDT = localZoneEnd.format(datetimeDTF);

                //Get title from appointment.
                String title = rs.getString("title");

                //Get type from appointment.
                String type = rs.getString("type");
                
                //Put Customer data into Customer object.
                Customer customer = new Customer(rs.getInt("customerId"), rs.getString("customerName"));
                String customerName = customer.getCustomerName();

                String user = rs.getString("createdBy");

                //Insert appointments into observablelist for AppointmentTableView if userName = createdBy.
                if (User.getUserID() == userID) {
                    scheduleOL.add(new Appointment(appointmentID, customerID, userID, title, description, location, contact, type, url, localStartDT, localEndDT, customerName, user));                    
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
