/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author Kody McLeod
 */
public class Country {
    private int countryID = 0;//auto generated
    private final String country;
    private final Date createDate;
    private final String createdBy;
    private Timestamp lastUpdate;
    private final String lastUpdatedBy;
    
    public Country(int countryID, String country, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy){
        this.countryID = countryID;//auto generated
        this.country = country;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;
    }
    
    //Getters.
    public int getCountryID(){
        return countryID;
    }
    
    public String getCountry(){
        return country;        
    }
    
    public Date getCreateDate(){
        return createDate;
    }
    
    public String getCreatedBy(){
        return createdBy;
    }
    
    public Timestamp getLastUpdate(){
        this.lastUpdate = Timestamp.valueOf(LocalDateTime.of(lastUpdate.toLocalDateTime().toLocalDate(), lastUpdate.toLocalDateTime().toLocalTime()));
        return this.lastUpdate;
    }
    
    public String getLastUpdatedBy(){
        return lastUpdatedBy;
    }
}
