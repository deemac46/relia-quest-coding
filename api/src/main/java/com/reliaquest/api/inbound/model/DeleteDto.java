package com.reliaquest.api.inbound.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteDto {

    @JsonProperty("name")
    private String name;
}
