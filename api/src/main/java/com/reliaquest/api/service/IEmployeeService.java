package com.reliaquest.api.service;

import com.reliaquest.api.inbound.model.RequestDto;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.service.model.Employee;

import java.io.IOException;
import java.util.List;

public interface IEmployeeService {

    List<Employee> getAllEmployees() throws ApiException;
    List<Employee> getEmployeesByNameSearch(String searchString) throws ApiException;
    Employee getEmployeeById(String id) throws ApiException;
    Integer getHighestSalaryOfEmployees() throws ApiException;
    List<String> getTopTenHighestEarningEmployeeNames() throws ApiException;
    Employee createEmployee(RequestDto employeeInput) throws IOException, InterruptedException, ApiException;
    String deleteEmployeeById(String id) throws IOException, InterruptedException, ApiException;


}
