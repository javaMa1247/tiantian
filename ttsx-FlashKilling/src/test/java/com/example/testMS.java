package com.example;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @program: -
 * @description:
 * @author: dx
 * @create: 2023/5/25 15:58
 */
public class testMS {
    @Test
    public void test1(){
        String dateTimeStr = "2023-05-26T16:00:00.000+00:00";
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeStr);
        LocalDate localDate = zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = localDate.format(formatter);
        System.out.println(new Date().getTime());
    }
}
