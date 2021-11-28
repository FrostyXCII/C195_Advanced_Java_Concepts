/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Kody McLeod
 */

//Class is used for creating reports
public class Reports {

    private String month;
    private int newAccount;
    private int consultation;
    private int followUp;
    private int closeAccount;
    private int email;
    private int phone;
    private int inperson;
    private int typesArray[];
     
    public Reports(String month, int typesArray[]) {
        this.month = month;
        this.typesArray = typesArray;
    }
    
    public Reports(String month, int newAccount, int consultation, int followUp, int closeAccount){
        this.month = month;
        this.newAccount = newAccount;
        this.consultation = consultation;
        this.followUp = followUp;
        this.closeAccount = closeAccount;
    }
    
    public Reports(String month, int email, int phone, int inperson){
        this.month = month;
        this.email = email;
        this.phone = phone;
        this.inperson = inperson;
    }

    //Getters.
    public String getMonth() {
        return this.month;
    }

    public int[] getTypesArray() {
        return this.typesArray;
    }

    public int getNewAccount(){
        return this.newAccount;
    }
    
    public int getConsultation(){
        return this.consultation;
    }
    
    public int getFollowUp(){
        return this.followUp;
    }
    
    public int getCloseAccount(){
        return this.closeAccount;
    }
    
    public int getEmail(){
        return this.email;
    }
    
    public int getPhone(){
        return this.phone;
    }
    
    public int getInperson(){
        return this.inperson;
    }
}
