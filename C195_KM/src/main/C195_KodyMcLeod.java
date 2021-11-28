/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.ConnectDB;
import java.sql.SQLException;

/**
 *
 * @author Kody McLeod
 */
public class C195_KodyMcLeod extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/Login.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            ConnectDB.makeConnection();
            launch(args);
            ConnectDB.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
