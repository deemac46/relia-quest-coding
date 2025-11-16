package com.reliaquest.api.service;

import com.reliaquest.api.inbound.model.RequestDto;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.outbound.EmployeeApi;
import com.reliaquest.api.service.model.Employee;
import com.reliaquest.api.service.model.EmployeeList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private final EmployeeApi employeeApi;
    private static final int DEFAULT_SALARY_NAMES_REQUIRED = 10;

    public EmployeeServiceImpl(EmployeeApi employeeApi) {
        this.employeeApi = employeeApi;
    }


    @Override
    public List<Employee> getAllEmployees() throws ApiException {
        log.info("Fetching all employees from EmployeeApi");

        return getEmployeeListFromApi();
    }
    
    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) throws ApiException {

        List<Employee> employeesList = getEmployeeListFromApi();

        // Find the employee
       return employeesList
               .stream()
               .filter(employee -> employee !=null
                       && employee.getEmployeeName() !=null
                       && employee.getEmployeeName().trim()
                       .equalsIgnoreCase(searchString.trim()))
               .toList();
    }
    
    @Override
    public Employee getEmployeeById(String id) throws ApiException {

        List<Employee> employeesList = getEmployeeListFromApi();

        return employeesList
                .stream()
                .filter(employee -> Objects.equals(employee.getId(), id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Integer getHighestSalaryOfEmployees() throws ApiException {

        List<Employee> employeesList = getEmployeeListFromApi();
        
        return employeesList.stream()
                //Map the salary field
                .map(Employee::getEmployeeSalary)
                //Using Integer comparator
                .max(Integer::compareTo)
                .orElse(0);
    }
    
    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() throws ApiException {

        List<Employee> employeesList = getEmployeeListFromApi();
        // priority queue to keep track of top 10 salaries
        PriorityQueue<Employee> topEmployees = new PriorityQueue<>(Comparator
                .comparing(Employee::getEmployeeSalary));

        employeesList.forEach(employee -> {
            // Add employee to the priority queue
           topEmployees.offer(employee);
           // If the size is greater than 10, we poll the employee with the lowest salary(head)
           if(topEmployees.size()> DEFAULT_SALARY_NAMES_REQUIRED){
               topEmployees.poll();
           }
        });
        
        //Reverse the list to have highest salary first due to priority queue ordering
        return addEmployeeToListAndReverse(topEmployees);
    }

    @Override
    public Employee createEmployee(RequestDto employeeInput) throws IOException, InterruptedException, ApiException {
        return employeeApi.addEmployee(employeeInput);
    }

    @Override
    public String deleteEmployeeById(String id) throws IOException, InterruptedException, ApiException {

        return employeeApi.deleteEmployeeById(id);
    }

    private List<Employee> getEmployeeListFromApi() throws ApiException {
        EmployeeList employees = employeeApi.getAllEmployees();

        if(employees == null || employees.getData() == null){
            log.warn("No employees found in EmployeeApi");
            return Collections.emptyList();
        }

        return employees.getData();
    }

    private List<String> addEmployeeToListAndReverse(PriorityQueue<Employee> topEmployees) {
        
        List<String> topSalaryNames = new ArrayList<>();
        //Add employees names to the list
        while (!topEmployees.isEmpty()){
            topSalaryNames.add(topEmployees.poll().getEmployeeName());
        }
        //Utilize Collections to reverse the list :)
        Collections.reverse(topSalaryNames);

        return topSalaryNames;
    }

}
