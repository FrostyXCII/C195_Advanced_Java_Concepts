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
public class Address {
    private int addressID = 0;//auto generated
    private final String address;
    private final String address2;
    private int cityID = 0;//auto generated
    private final String postalCode;
    private final String phone;
    private final Date createDate;
    private final String createdBy;
    private Timestamp lastUpdate;
    private final String lastUpdatedBy;
    
    public Address(int addressID, String address, String address2, int cityID, String postalCode, String phone, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdateBy){
        
        this.addressID = addressID;//auto generated
        this.address = address;
        this.address2 = address2;
        this.cityID = cityID;//auto generated
        this.postalCode = postalCode;
        this.phone = phone;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdateBy;               
    }
    
    //Getters.
    public int getAddressID(){
        return this.addressID;
    }
    
    public String getAddress(){
        return this.address;
    }
    
    public String getAddress2(){
        return this.address2;
    }
    
    public int getCityID(){
        return this.cityID;        
    }
    
    public String getPostalCode(){
        return this.postalCode;
    }
    
    public String getPhone(){
        return this.phone;
    }
    
    public Date getCreateDate(){
        return this.createDate;                
    }  
    
    public String getCreatedBy(){
        return this.createdBy;
    }
    
    public Timestamp getLastUpdate(){
        this.lastUpdate = Timestamp.valueOf(LocalDateTime.of(lastUpdate.toLocalDateTime().toLocalDate(), lastUpdate.toLocalDateTime().toLocalTime()));
        return this.lastUpdate;
    }
    
    public String getLastUpdatedBy(){
        return this.lastUpdatedBy;
    }
}
