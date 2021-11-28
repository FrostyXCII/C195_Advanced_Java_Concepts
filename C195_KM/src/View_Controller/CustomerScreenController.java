/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Customer;
import Model.User;
import util.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kody McLeod
 */
public class CustomerScreenController implements Initializable {
    
    @FXML
    private Label CustomerLabel;
    @FXML
    private TableView<Customer> CustomerTable;
    @FXML
    private TableColumn<Customer, Integer> CustomerCustomerIDColumn;
    @FXML
    private TableColumn<Customer, String> CustomerCustomerNameColumn;
    @FXML
    private TableColumn<Customer, String> CustomerPhoneColumn;
    @FXML
    private TextField CustomerCustomerIDTextField;
    @FXML
    private ComboBox<String> CustomerCityComboBox;
    @FXML
    private ComboBox<String> CustomerCountryComboBox;
    @FXML
    private Button CustomerSaveButton;
    @FXML
    private Button CustomerCancelButton;
    @FXML
    private Button CustomerDeleteButton;
    @FXML
    private TextField CustomerCustomerNameTextField;
    @FXML
    private TextField CustomerAddressTextField;
    @FXML
    private TextField CustomerAddress2TextField;
    @FXML
    private TextField CustomerPostalCodeTextField;
    @FXML
    private TextField CustomerPhoneTextField;
    @FXML
    private RadioButton CustomerActiveRadioButton;
    @FXML
    private RadioButton CustomerInactiveRadioButton;
    @FXML
    private ToggleGroup RadioButtonToggleGroup;
    @FXML
    private Button CustomerBackButton;

    Parent root;
    Stage stage;

    //Creates ObservableLists.
    ObservableList<Customer> customerOL = FXCollections.observableArrayList();
    ObservableList<String> cityOptions = FXCollections.observableArrayList();
    ObservableList<String> countryOptions = FXCollections.observableArrayList();

    private static Customer selectedCustomer = new Customer();
    private boolean customerUpdate = false; //Used to determine whether to UPDATE customer in the database.
    private boolean customerAdd = false; //Used to determine whether to INSERT customer in the database.
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Populates CustomerTable with values.
        PropertyValueFactory<Customer, String> custNameFactory = new PropertyValueFactory<>("CustomerName");
        PropertyValueFactory<Customer, String> custPhoneFactory = new PropertyValueFactory<>("CustomerPhone"); //String value "CustomerPhone" calls getCustomerPhone method.
        PropertyValueFactory<Customer, Integer> custCustomerIDFactory = new PropertyValueFactory<>("CustomerID");
        CustomerCustomerIDColumn.setCellValueFactory(custCustomerIDFactory);
        CustomerCustomerNameColumn.setCellValueFactory(custNameFactory);
        CustomerPhoneColumn.setCellValueFactory(custPhoneFactory);

        CustomerCustomerIDTextField.setText("Auto Generated");

        //Disables input for CustomerID since its auto-generated.
        disableCustomerFields();

        try {
            RadioButtonToggleGroup = new ToggleGroup();
            CustomerActiveRadioButton.setToggleGroup(RadioButtonToggleGroup);
            CustomerInactiveRadioButton.setToggleGroup(RadioButtonToggleGroup);
            CustomerActiveRadioButton.setSelected(false);
            CustomerInactiveRadioButton.setSelected(false);
            selectedCustomer.setCustomerActive(1);

            updateCustomerTable();
            try {
                fillCityComboBox();
            } catch (Exception ex) {
                Logger.getLogger(CustomerScreenController.class.getName()).log(Level.INFO, null, ex);
            }
            try {
                fillCountryComboBox();
            } catch (Exception e) {
                Logger.getLogger(CustomerScreenController.class.getName()).log(Level.INFO, null, e);
            }
        } catch (Exception e) {
            Logger.getLogger(CustomerScreenController.class.getName()).log(Level.INFO, null, e);
        }
        //Lambda that listens for mouse click on item in Customer Table.
        CustomerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    try {
                        customerListener(newValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    public void updateCustomerTable() {
        try {
            customerOL.clear();
            //Creates statement object.
            Statement stmt = ConnectDB.conn.createStatement();

            //Write SQL statement (columns from two tables).
            String sqlStatement = "SELECT customerId, customerName, phone,  active FROM customer, address WHERE customer.addressId = address.addressId ORDER BY customer.customerName";

            //Executes statement and create resultset object.
            ResultSet result = stmt.executeQuery(sqlStatement);

            //Get all records from resultset objects.
            while (result.next()) {
                Customer cust = new Customer();
                cust.setCustomerID(result.getInt("customerId"));
                cust.setCustomerName(result.getString("customerName"));
                cust.setCustomerPhone(result.getString("phone"));            
                customerOL.addAll(cust);
            }
        
            CustomerTable.setItems(customerOL);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTextFields() {
        CustomerLabel.setText("");
        CustomerCustomerIDTextField.setText("");
        CustomerCustomerNameTextField.setText("");
        CustomerAddressTextField.setText("");
        CustomerAddress2TextField.setText("");
        CustomerCityComboBox.setValue("");
        CustomerCountryComboBox.setValue("");
        CustomerPostalCodeTextField.setText("");
        CustomerPhoneTextField.setText("");
        CustomerInactiveRadioButton.setSelected(false);
        CustomerActiveRadioButton.setSelected(false);
    }

    //Populates CityComboBox with all available cities.
    public void fillCityComboBox() {
        try {
            //Write SQL statement. 
            String sqlStatement = "SELECT city FROM city";

            //Create statement object.
            PreparedStatement pst = ConnectDB.makeConnection().prepareStatement(sqlStatement);

            //Execute statement and create resultset object.
            ResultSet result = pst.executeQuery(sqlStatement);

            //Get all records from resultset object.
            while (result.next()) {
                Customer cust = new Customer();
                cust.setCustomerCity(result.getString("city"));
                cityOptions.add(cust.getCustomerCity());
                CustomerCityComboBox.setItems(cityOptions);

            }
            
            pst.close();
            result.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Populate CountryComboBox with all available countries.
    public void fillCountryComboBox() {
        try {
            //Create statement object.
            Statement stmt = ConnectDB.makeConnection().createStatement();

            //Write SQL statement (columns from two tables).
            String sqlStatement = "SELECT country FROM country";

            //Execute statement and create resultset object.
            ResultSet result = stmt.executeQuery(sqlStatement);

            //Get all records from resultset object.
            while (result.next()) {
                Customer cust = new Customer();
                cust.setCustomerCountry(result.getString("country"));
                countryOptions.add(cust.getCustomerCountry());
                CustomerCountryComboBox.setItems(countryOptions);
            }
        
            stmt.close();
            result.close();
        
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void CustomerSaveButtonHandler(MouseEvent event) {
        try {
            if (CustomerCustomerNameTextField.getText() != null && customerAdd || customerUpdate) {
                if (validCustomer()) {
                    if (customerAdd) {
                        saveCustomer();
                        clearTextFields();
                        updateCustomerTable();
                    } else if (customerUpdate) {
                        updateCustomer();
                        clearTextFields();
                        updateCustomerTable();
                    }
                }
            } else {
                
                //Shows the error message.
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR");
                alert.setHeaderText("Select Customer!");
                alert.setContentText("No customer has been selected to save.");
                Optional<ButtonType> result = alert.showAndWait(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Saves new customer.
    private void saveCustomer() {

        try {
            int newAddressID = -1;
            //Insert new address into Database.
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, CustomerAddressTextField.getText());
            ps.setString(2, CustomerAddress2TextField.getText());
            ps.setInt(3, getCityID(CustomerCityComboBox.getValue()));
            ps.setString(4, CustomerPostalCodeTextField.getText());
            ps.setString(5, CustomerPhoneTextField.getText());
            ps.setString(6, User.getUsername());
            ps.setString(7, User.getUsername());
            int result = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            
            if (rs.next()) {
                
                newAddressID = rs.getInt(1);
            }
            
            //Insert new customer into DB 
            PreparedStatement psc = ConnectDB.makeConnection().prepareStatement("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");

            psc.setString(1, CustomerCustomerNameTextField.getText());
            psc.setInt(2, newAddressID);
            psc.setInt(3, selectedCustomer.getCustomerActive());
            psc.setString(4, User.getUsername());
            psc.setString(5, User.getUsername());
            int results = psc.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        clearTextFields();
        disableCustomerFields();
        updateCustomerTable();
        customerAdd = false;
        customerUpdate = false;
    }

    private void deleteCustomer(Customer customer) {
        
        try {
            
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("DELETE customer.*, address.* from customer, address WHERE customer.customerId = ? AND customer.addressId = address.addressId");
            ps.setInt(1, customer.getCustomerID());
            int result = ps.executeUpdate();           
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        clearTextFields();
        disableCustomerFields();
        updateCustomerTable();
    }
    
    //Updates current customer with all changes.
    private void updateCustomer() {

        try {
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("UPDATE address, customer, city, country "
                    + "SET address = ?, address2 = ?, address.cityId = ?, postalCode = ?, phone = ?, address.lastUpdate = CURRENT_TIMESTAMP, address.lastUpdateBy = ? "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");

            ps.setString(1, CustomerAddressTextField.getText());
            ps.setString(2, CustomerAddress2TextField.getText());
            ps.setInt(3, getCityID(CustomerCityComboBox.getValue()));
            ps.setString(4, CustomerPostalCodeTextField.getText());
            ps.setString(5, CustomerPhoneTextField.getText());
            ps.setString(6, User.getUsername());
            ps.setString(7, CustomerCustomerIDTextField.getText());

            int result = ps.executeUpdate();

            PreparedStatement psc = ConnectDB.makeConnection().prepareStatement("UPDATE customer, address, city "
                    + "SET customerName = ?, customer.active = ?, customer.lastUpdate = CURRENT_TIMESTAMP, customer.lastUpdateBy = ? "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId");

            psc.setString(1, CustomerCustomerNameTextField.getText());
            psc.setInt(2, selectedCustomer.getCustomerActive());
            psc.setString(3, User.getUsername());
            psc.setString(4, CustomerCustomerIDTextField.getText());
            int results = psc.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        clearTextFields();
        disableCustomerFields();
        updateCustomerTable();
        customerAdd = false;
        customerUpdate = false;
    }

    //Checks to see if Customer data fields are valid before save/update.
    private boolean validCustomer() {

        String customerName = CustomerCustomerNameTextField.getText().trim();
        String address = CustomerAddressTextField.getText().trim();
        String address2 = CustomerAddress2TextField.getText().trim();
        String city = CustomerCityComboBox.getValue().trim();
        String country = CustomerCountryComboBox.getValue().trim();
        String postalCode = CustomerPostalCodeTextField.getText().trim();
        String phone = CustomerPhoneTextField.getText().trim();
        boolean active = CustomerActiveRadioButton.isSelected();
        boolean inactive = CustomerInactiveRadioButton.isSelected();

        String errorMessage = "";
        
        //First checks to see if inputs are null.
        if (!active & !inactive) {
            errorMessage += "You must select Active or Inactive.\n";
        }
        if (customerName == null || customerName.length() == 0) {
            errorMessage += "Please enter Customer Name.\n";
        }
        if (address == null || address.length() == 0) {
            errorMessage += "Please enter an address.\n";
        }
        if (address2.trim() == null || address2.length() == 0) {
            errorMessage += "Please enter N/A if there is no additional address.\n";
        }
        if (city == null) {
            errorMessage += "Please Select a City.\n";
        }
        if ((city.equals("Phoenix") || city.equals("New York") || city.equals("Los Angeles")) && (country.equals("Canada"))){
            errorMessage += "These cities are located in the United States, not Canada.";
        }
        if ((city.equals("Phoenix") || city.equals("New York") || city.equals("Los Angeles")) && (country.equals("Norway"))){
            errorMessage += "These cities are located in the United States, not Norway.";
        }
        if ((city.equals("Toronto") || city.equals("Vancouver")) && (country.equals("United States"))){
            errorMessage += "These cities are located in Canada, not the United States.";
        }
        if ((city.equals("Toronto") || city.equals("Vancouver")) && (country.equals("Norway"))){
            errorMessage += "These cities are located in Canada, not Norway.";
        }
        if ((city.equals("Oslo")) && (country.equals("United States"))){
            errorMessage += "This city is located in Norway, not the United States.";
        }
        if ((city.equals("Oslo")) && (country.equals("Canada"))){
            errorMessage += "This city is located in Norway, not Canada.";
        }
        if (country == null) {
            errorMessage += "Please Select a Country.\n";
        }
        if (postalCode == null || postalCode.length() == 0) {
            errorMessage += "Please enter a valid Postal Code.\n";
        } else if (postalCode.length() > 10 || postalCode.length() < 5) {
            errorMessage += "Postal Code must be between 5 and 10 characters.\n";
        }
        if (phone == null || phone.length() == 0) {
            errorMessage += "Please enter a Phone Number.";
        } else if (phone.length() < 6 || phone.length() > 15) {
            errorMessage += "Please enter a valid phone number.\n";
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Invalid Customer");
            alert.setContentText(errorMessage);
            Optional<ButtonType> result = alert.showAndWait();

            return false;
        }
    }

    private int getCityID(String city) {
        
        int cityID = -1;
        
        try {

            //Create statement object.
            Statement statement = ConnectDB.makeConnection().createStatement();

            //Write SQL statement.
            String sqlStatement = "SELECT cityID FROM city WHERE city.city ='" + city + "'";

            //Create resultset object.
            ResultSet result = statement.executeQuery(sqlStatement);

            while (result.next()) {
                cityID = result.getInt("cityId");
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return cityID;
    }

    //Prevents inputs in customer fields when not adding or updating a customer.
    public void disableCustomerFields(){
        CustomerActiveRadioButton.setDisable(true);
        CustomerInactiveRadioButton.setDisable(true);
        CustomerCustomerIDTextField.setDisable(true);
        CustomerCustomerNameTextField.setDisable(true);
        CustomerAddressTextField.setDisable(true);
        CustomerAddress2TextField.setDisable(true);
        CustomerCityComboBox.setDisable(true);
        CustomerCountryComboBox.setDisable(true);
        CustomerPostalCodeTextField.setDisable(true);
        CustomerPhoneTextField.setDisable(true);
        CustomerSaveButton.setDisable(true);
        CustomerCancelButton.setDisable(true);
        CustomerDeleteButton.setDisable(true);
    }
    
    //Allows inputs to add/update customer.
    public void enableCustomerFields(){
        CustomerActiveRadioButton.setDisable(false);
        CustomerInactiveRadioButton.setDisable(false);
        CustomerCustomerIDTextField.setDisable(false);
        CustomerCustomerIDTextField.setEditable(false);
        CustomerCustomerNameTextField.setDisable(false);
        CustomerAddressTextField.setDisable(false);
        CustomerAddress2TextField.setDisable(false);
        CustomerCityComboBox.setDisable(false);
        CustomerCountryComboBox.setDisable(false);
        CustomerPostalCodeTextField.setDisable(false);
        CustomerPhoneTextField.setDisable(false);
        CustomerSaveButton.setDisable(false);
        CustomerCancelButton.setDisable(false);
        CustomerDeleteButton.setDisable(false);
    }
    
    //Method is called when customer table listener detects customer selection to update customer.
    public void customerListener(Customer customer) {

        try {
        
            Customer cust = new Customer();
            cust = customer;
            String custName = cust.getCustomerName();
            int custId = cust.getCustomerID();
            ObservableList<Customer> customerOL = FXCollections.observableArrayList();

            CustomerLabel.setText("Update");
            customerUpdate = true;
            customerAdd = false;
            enableCustomerFields();

            //Create statement object.
            PreparedStatement ps = ConnectDB.makeConnection().prepareStatement("SELECT * FROM customer, address, city, country "
                    + "WHERE customer.customerId = ? AND customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId");        
        
            //Execute statement and create resultset object.
            ps.setInt(1, custId);
            ResultSet result = ps.executeQuery();
        
            while (result.next()) {
            

                CustomerCustomerIDTextField.setText(Integer.toString(result.getInt("customerId")));
                CustomerCustomerNameTextField.setText(result.getString("customerName"));
                CustomerAddressTextField.setText(result.getString("address"));
                CustomerAddress2TextField.setText(result.getString("address2"));
                CustomerCityComboBox.setValue(result.getString("city"));
                CustomerCountryComboBox.setValue(result.getString("country"));
                CustomerPostalCodeTextField.setText(result.getString("postalCode"));
                CustomerPhoneTextField.setText(result.getString("phone"));
            
                if (result.getInt("active") == 0) {
                
                    CustomerActiveRadioButton.setSelected(false);
                    CustomerInactiveRadioButton.setSelected(true);
                
                } else {
                
                    CustomerActiveRadioButton.setSelected(true);
                    CustomerInactiveRadioButton.setSelected(false);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void CustomerCancelButtonHandler(MouseEvent event) {
         
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Required");
        alert.setHeaderText("Confirm Cancel");
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {

            customerUpdate = false;
            customerAdd = false;
            clearTextFields();
            disableCustomerFields();
        }    
    }

    @FXML
    private void CustomerAddButtonHandler(MouseEvent event) {
        
        clearTextFields();
        CustomerLabel.setText("Add");
        CustomerCustomerIDTextField.setText("Auto Generated");
        customerAdd = true;
        customerUpdate = false;
        enableCustomerFields();
    }

    @FXML
    private void CustomerDeleteButtonHandler(MouseEvent event) {
        
        if (CustomerTable.getSelectionModel().getSelectedItem() != null) {

            Customer cust = CustomerTable.getSelectionModel().getSelectedItem();
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Required");
            alert.setHeaderText("Confirm DELETE");
            alert.setContentText("Are you sure you want to DELETE this customer?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
                
                deleteCustomer(cust);

                clearTextFields();
                disableCustomerFields();
                updateCustomerTable();
                
            }
            
        } else {
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ERROR");
            alert.setHeaderText("Select customer!");
            alert.setContentText("No customer was selected to delete.");
            Optional<ButtonType> result = alert.showAndWait();
        }
    }

    @FXML
    private void CustomerActiveRadioButtonHandler(MouseEvent event) {
        
        selectedCustomer.setCustomerActive(1);
    }

    @FXML
    private void CustomerInactiveRadioButtonHandler(MouseEvent event) {
        
        selectedCustomer.setCustomerActive(0);
    }

    @FXML
    private void CustomerBackButtonHandler(MouseEvent event) {
        
        try {
            root = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
            stage = (Stage) CustomerBackButton.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
