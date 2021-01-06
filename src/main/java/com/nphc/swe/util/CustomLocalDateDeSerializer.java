package com.nphc.swe.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nphc.swe.exception.DateDeSerializerException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateDeSerializer extends StdDeserializer<LocalDate> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected CustomLocalDateDeSerializer(Class<LocalDate> t) {
        super(t);
    }

    public CustomLocalDateDeSerializer() {
        this(null);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        try {
            return LocalDate.parse(p.getText(), formatter);
        } catch (Exception e) {
            throw new DateDeSerializerException(e.getMessage());
        }
    }
}
