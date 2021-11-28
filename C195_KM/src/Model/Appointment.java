/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author Kody McLeod
 */
public class Appointment {
    
    private int appointmentID;//auto generated
    private int customerID;//auto generated
    private int userID;//auto generated    
    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;
    private String startTime;
    private String endTime;
    private String customerName;    
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    
    public Appointment(int appointmentID, int customerID, int userID, String title, String description, String location, 
                String contact, String type, String url, String startTime, String endTime, Date createDate, String createdBy, Timestamp lastUpdate, String lastUpdatedBy){
        
        this.appointmentID = appointmentID;//auto generated
        this.customerID = customerID;//auto generated
        this.userID = userID;//auto generated
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdatedBy = lastUpdatedBy;        
    }
    
    public Appointment(int appointmentID, String startTime, String endTime, String title, String type, int customerId, String customerName, String user){
        this.appointmentID = appointmentID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.type = type;
        this.customerID = customerId;
        this.customerName = customerName;
        this.createdBy = user;
    }
    
    public Appointment(int appointmentID, int customerID, int userID, String title, String description, String location, String contact, String type, 
                String url, String startTime, String endTime, String customerName, String user){
        
        this.appointmentID = appointmentID;//auto generated
        this.customerID = customerID;//auto generated
        this.userID = userID;//auto generated
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerName = customerName;
        this.createdBy = user;       
    }
    
    public Appointment(String startTime, String endTime, String title, String type, String customer, String consultant) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.type = type;
        this.customerName = customer;
        this.createdBy = consultant; //consultant equals User
    }
    
    public Appointment(){
    }
        
    //Getters.
    public int getAppointmentID(){
        return this.appointmentID;
    }
    
    public int getCustomerID(){
        return this.customerID;
    }
    
    public int getUserID(){
        return this.userID;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public String getDescription(){
        return this.description;
    }
    
    public String getLocation(){
        return this.location;
    }
    
    public String getContact(){
        return this.contact;
    }
    
    public String getType(){
        return this.type;
    }
    
    public String getUrl(){
        return this.url;
    }
    
    public String getStart(){        
        return this.startTime;
    }
    
    public String getEnd(){       
        return this.endTime;
    }
    
    public Date getCreateDate(){
        return this.createDate;
    }
    
    public String getCreatedBy(){
        return this.createdBy;
    }
    
    public Timestamp getLastUpdate(){
        return this.lastUpdate;
    }
    
    public String getLastUpdatedBy(){
        return this.lastUpdatedBy;
    }
    
    public String getCustomerName(){
        return this.customerName;
    }
    
    public String getConsultantName(){
        return this.createdBy;
    }
     public Customer getCustomer() {
        return customer;
    }
    
    //Setters.
    public void setCustomerName(String customerName){
       this.customerName = customerName;
    }
    
    public void setConsultant(String consultant){
        this.createdBy = consultant;
    }   
}