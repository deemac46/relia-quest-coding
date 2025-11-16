package com.reliaquest.api;

import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.inbound.model.RequestDto;
import com.reliaquest.api.outbound.EmployeeApi;
import com.reliaquest.api.service.EmployeeServiceImpl;
import com.reliaquest.api.service.model.Employee;
import com.reliaquest.api.service.model.EmployeeList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class ApiApplicationTest {


    @Mock
    private EmployeeApi employeeApi;
    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
    }
    @Test
    void testGetAllEmployees() throws ApiException {
        // Arrange
        when(employeeApi.getAllEmployees()).thenReturn(createEmployeeList());
        // Act
        List<Employee> result = employeeService.getAllEmployees();
        // Assert
        assertNotNull(result);
        assertEquals(11, result.size());
        assertEquals("Employee 0", result.get(0).getEmployeeName());
    }
    @Test
    void testGetEmployeesByNameSearch() throws ApiException {
        // Arrange
        when(employeeApi.getAllEmployees()).thenReturn(createEmployeeList());
        // Act
        List<Employee> result = employeeService.getEmployeesByNameSearch("Employee 2");
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Employee 2", result.get(0).getEmployeeName());

    }
    @Test
    void testGetEmployeeById() throws ApiException {
        // Arrange
        when(employeeApi.getAllEmployees()).thenReturn(createEmployeeList());

        // Act
        Employee result = employeeService.getEmployeeById("0");

        // Assert
        assertNotNull(result);
        assertEquals("Employee 0", result.getEmployeeName());

    }
    @Test
    void testGetHighestSalaryOfEmployees() throws ApiException {
        // Arrange
        when(employeeApi.getAllEmployees()).thenReturn(createEmployeeList());
        // Act
        Integer result = employeeService.getHighestSalaryOfEmployees();
        // Assert
        assertEquals(110000, result);
    }
    @Test
    void testGetTopTenHighestEarningEmployeeNames() throws ApiException {
        // Arrange
        when(employeeApi.getAllEmployees()).thenReturn(createEmployeeList());
        // Act
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();
        // Assert
        assertNotNull(result);
        assertEquals(10, result.size());
        assertEquals("Employee 10", result.get(0));
    }
    @Test
    void testCreateEmployee_Success() throws Exception {
        // Arrange
        RequestDto requestDto = new RequestDto();
        requestDto.setName("Employee new");
        requestDto.setAge(30);
        requestDto.setSalary(50000);

        Employee expectedEmployee = new Employee();
        expectedEmployee.setId("109");
        expectedEmployee.setEmployeeName("Employee new");
        expectedEmployee.setEmployeeSalary(50000);

        when(employeeApi.addEmployee(requestDto)).thenReturn(expectedEmployee);
        // Act
        Employee result = employeeService.createEmployee(requestDto);
        // Assert
        assertNotNull(result);
        assertEquals("109", result.getId());
        assertEquals("Employee new", result.getEmployeeName());
        assertEquals(50000, result.getEmployeeSalary());
        }
    @Test
    void testDeleteEmployeeById_Success() throws Exception {
        // Arrange
        String employeeId = "001";
        when(employeeApi.deleteEmployeeById(employeeId)).thenReturn(employeeId);
        // Act
        String result = employeeService.deleteEmployeeById(employeeId);
        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result);
     }

    private EmployeeList createEmployeeList() {

        EmployeeList employees = new EmployeeList();
        employees.setData(new ArrayList<>());

        for (int i = 0; i<11; i++) {
            Employee emp = new Employee();
            emp.setId(String.valueOf(i));
            emp.setEmployeeName("Employee " + i);
            emp.setEmployeeSalary(10000 + (i * 10000));
            employees.getData().add(emp);
        }

        return employees;
    }



}
