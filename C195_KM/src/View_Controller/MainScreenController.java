/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import util.ConnectDB;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Kody McLeod
 */
public class MainScreenController implements Initializable {

    
    @FXML
    private Button MainCustomersButton;
    
    Parent root;
    Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void MainCustomersButtonHandler(MouseEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("CustomerScreen.fxml"));
            stage = (Stage)MainCustomersButton.getScene().getWindow();
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void MainAppointmentsButtonHandler(MouseEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("AppointmentsMain.fxml"));
            stage = (Stage)MainCustomersButton.getScene().getWindow();
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void MainReportsButtonHandler(MouseEvent event) {
        try{
            root = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            stage = (Stage)MainCustomersButton.getScene().getWindow();
            Scene scene = new Scene(root);
        
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void MainExitButtonHandler(MouseEvent event) {
        try{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);            
            alert.setTitle("Confirmation Required");
            alert.setHeaderText("Confirm Exit");
            alert.setContentText("Are you sure you want to exit?");
            Optional<ButtonType> result = alert.showAndWait();
       
            if (result.get() == ButtonType.OK) {         
                ConnectDB.closeConnection();
                System.exit(0);
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}
