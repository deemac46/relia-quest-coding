package com.reliaquest.api.service.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeList {

    private List<Employee> data;
    private String status;

}
