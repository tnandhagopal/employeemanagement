package com.nphc.swe.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nphc.swe.model.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonSerialize
@JsonDeserialize
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersDataResponse {
    private List<UserDTO> results;
}
