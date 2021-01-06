package com.nphc.swe.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nphc.swe.exception.UserDeSerializerException;
import com.nphc.swe.model.dto.UserDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomUserDeSerializer extends StdDeserializer<UserDTO> {

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    protected CustomUserDeSerializer(Class<UserDTO> t) {
        super(t);
    }

    public CustomUserDeSerializer() {
        this(null);
    }

    @Override
    public UserDTO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        String id = node.get("id").asText();
        String login = node.get("login").asText();
        String name = node.get("name").asText();

        BigDecimal salary;
        try {
            salary = new BigDecimal(node.get("salary").asText().trim());
        } catch (Exception e) {
            throw new UserDeSerializerException("Invalid salary");
        }

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(node.get("startDate").asText().trim(), formatter);
        } catch (Exception e) {
            throw new UserDeSerializerException("Invalid date");
        }

        return UserDTO.builder()
                .id(id)
                .login(login)
                .name(name)
                .salary(salary)
                .startDate(startDate)
                .build();

    }
}
