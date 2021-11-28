/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Kody McLeod
 */
public class City {
    
    private final IntegerProperty cityId;
    private final StringProperty city;
    private final IntegerProperty countryId;
    private final StringProperty createdBy;
    private final StringProperty lastUpdateBy;
    
    City(int cityId, String city, int countryId, Date createDate, String createdBy, Date lastUpdate, String lastUpdateBy){
        this.cityId = new SimpleIntegerProperty(cityId);
        this.city = new SimpleStringProperty(city);
        this.countryId = new SimpleIntegerProperty(countryId);
        this.createdBy = new SimpleStringProperty(createdBy);
        this.lastUpdateBy = new SimpleStringProperty(lastUpdateBy);
        
    }
    
    //Getters.
    public IntegerProperty getCityIdProperty(){
        return cityId;
    }
    
}
