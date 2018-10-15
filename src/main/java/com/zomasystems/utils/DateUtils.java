/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.utils;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateUtils {

    public static final SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMyyyy");
    public static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HHmmss");

    public String generateDateTimeString(){
        Date date = new Date();
        return dateFormatter.format(date) + timeFormatter.format(date);
    }
}
