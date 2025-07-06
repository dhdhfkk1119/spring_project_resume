package com.join.spring_resume.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String format(LocalDateTime time) {
        if (time == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return time.format(formatter);
    }


    // LocalDateTime 용 시간 포맷
    public static String formatDate(LocalDateTime time) {
        if (time == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return time.format(formatter);
    }
    
    // timeStamp 용 시간 포맷
    public static String timestampFormat(Timestamp time){
        // Board 엔티티에 선언된 Timestamp를 Date 객체로 변화
        Date currentData = new Date(time.getTime());
        return DateFormatUtils.format(currentData,"yyyy-MM-dd HH:MM");
    }
}
