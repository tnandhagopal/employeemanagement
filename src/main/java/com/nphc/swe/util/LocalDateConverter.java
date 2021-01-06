package com.nphc.swe.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends AbstractBeanField {
    @Override
    protected Object convert(String s) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        LocalDate parse;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
             parse = LocalDate.parse(s, formatter);
        }catch (Exception e){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
            parse = LocalDate.parse(s, formatter);
        }
        return parse;
    }
}
