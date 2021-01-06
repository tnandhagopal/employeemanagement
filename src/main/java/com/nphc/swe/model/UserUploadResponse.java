package com.nphc.swe.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

@Data
@JsonSerialize
@JsonDeserialize
@Builder
public class UserUploadResponse {
    private String message;
}
