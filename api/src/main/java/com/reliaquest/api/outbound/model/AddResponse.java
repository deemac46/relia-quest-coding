package com.reliaquest.api.outbound.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reliaquest.api.service.model.Employee;
import lombok.Data;

@Data
public class AddResponse {

    @JsonProperty("data")
    private Employee data;

    @JsonProperty("status")
    private String status;

}
