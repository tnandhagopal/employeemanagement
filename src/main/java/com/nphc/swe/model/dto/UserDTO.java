package com.nphc.swe.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nphc.swe.persistence.entity.User;
import com.nphc.swe.util.CustomLocalDateSerializer;
import com.nphc.swe.util.CustomUserDeSerializer;
import com.nphc.swe.util.LocalDateConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = CustomUserDeSerializer.class)
@JsonSerialize
public class UserDTO {

    @CsvBindByName
    private String id;

    @CsvBindByName
    private String login;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private BigDecimal salary;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @CsvCustomBindByName(column = "startDate", converter = LocalDateConverter.class)
    private LocalDate startDate;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private String updatedBy;

    @JsonIgnore
    private LocalDateTime updatedAt;

    public static UserDTO from(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .salary(user.getSalary())
                .startDate(user.getStartDate())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static User to(UserDTO user) {
        if (user == null) return null;

        return User.builder()
                .id(user.getId())
                .login(user.getLogin())
                .name(user.getName())
                .salary(user.getSalary())
                .startDate(user.getStartDate())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
