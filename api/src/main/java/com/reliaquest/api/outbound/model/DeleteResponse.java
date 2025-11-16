package com.reliaquest.api.outbound.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteResponse {

    @JsonProperty("data")
    private boolean data;
    @JsonProperty("status")
    private String status;

}
