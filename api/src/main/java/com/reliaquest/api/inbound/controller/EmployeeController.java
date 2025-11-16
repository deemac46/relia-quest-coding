package com.reliaquest.api.inbound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.inbound.model.RequestDto;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.service.EmployeeServiceImpl;
import com.reliaquest.api.service.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Controller
public class EmployeeController implements IEmployeeController {

    private final EmployeeServiceImpl employeeService;
    private final ObjectMapper objectMapper;

    public EmployeeController(@Autowired EmployeeServiceImpl employeeService, @Autowired ObjectMapper objectMapper) {
        this.employeeService = employeeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws ApiException {
        log.info("Fetching all employees");
        List<Employee> employees = employeeService.getAllEmployees();

        return employees.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) throws ApiException {
        if (searchString == null || searchString.isBlank()) {
            log.warn("Search string is null or blank");
            return ResponseEntity.badRequest().build();
        }

        log.info("Fetching employees with name containing: {}", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);

        return employees.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) throws ApiException {
        if (id == null || id.isBlank()) {
            log.warn("Employee ID is null or blank");
            return ResponseEntity.badRequest().build();
        }

        log.info("Fetching employee with ID: {}", id);
        Employee employee = employeeService.getEmployeeById(id);

        return employee != null
                ? ResponseEntity.ok(employee)
                : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() throws ApiException {
        log.info("Fetching highest salary from all employees");
        Integer salary = employeeService.getHighestSalaryOfEmployees();

        return salary != null
                ? ResponseEntity.ok(salary)
                : ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() throws ApiException {
        log.info("Fetching top 10 highest salary employees");
        List<String> names = employeeService.getTopTenHighestEarningEmployeeNames();

        return names.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(names);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody Object employeeInput) throws IOException, InterruptedException, ApiException {
        log.info("Creating new employee: {}", employeeInput);

        if (employeeInput == null) {
            log.warn("Employee input is null");

            return ResponseEntity.badRequest().build();
        }

        RequestDto employee = handleEmployeeInput(employeeInput);
        Employee created = employeeService.createEmployee(employee);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) throws IOException, InterruptedException, ApiException {
        if (id == null || id.isBlank()) {
            log.warn("Employee ID is null or blank");
            return ResponseEntity.badRequest().build();
        }

        log.info("Deleting employee with ID: {}", id);
        String result = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(result);
    }

    private RequestDto handleEmployeeInput(Object employeeInput) {
        log.info("Mapping employee input: {}", employeeInput);
        try {
            return objectMapper.convertValue(employeeInput, RequestDto.class);
        } catch (IllegalArgumentException e) {
            log.error("Invalid employee data format", e);
            throw new IllegalArgumentException("Invalid employee data format", e);
        }
    }
}
