/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *
 * @author Kody McLeod
 */
public class DateTime {
    
    public static java.sql.Timestamp getTimeStamp() {
        
        ZoneId zoneid = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.now(zoneid);
        java.sql.Timestamp timeStamp = Timestamp.valueOf(localDateTime);
        return timeStamp;
    }
    
    public static java.sql.Date getDate() {
        
        java.sql.Date date = java.sql.Date.valueOf(LocalDate.now());
        return date;
    }
}
