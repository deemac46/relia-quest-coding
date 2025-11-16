package com.reliaquest.api.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.inbound.model.DeleteDto;
import com.reliaquest.api.inbound.model.RequestDto;
import com.reliaquest.api.outbound.model.AddResponse;
import com.reliaquest.api.outbound.model.DeleteResponse;
import com.reliaquest.api.service.model.Employee;
import com.reliaquest.api.service.model.EmployeeList;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
@Service
public class EmployeeApi {


    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:8112/api/v1";

    public EmployeeApi() {

    }
    public EmployeeList getAllEmployees() throws ApiException {
        HttpRequest request = HttpRequest.newBuilder()
                .headers("Accept", "application/json")
                .uri(java.net.URI.create(baseUrl + "/employee"))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                log.info("Successfully fetched all employees from EmployeeApi");
                return objectMapper.readValue(response.body(), EmployeeList.class);
            } else {
                log.error("Unexpected error fetching employees. Status: {}", response.statusCode());
                throw new ApiException("Failed to fetch employees", response.statusCode());
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while fetching employees", e);
            throw new ApiException("Request interrupted", 0, e);
        } catch (IOException e) {
            log.error("IO exception while fetching employees", e);
            throw new ApiException("Network error occurred", 0, e);
        }
    }
    public Employee addEmployee(RequestDto employee) throws ApiException {
        String requestBody = toJson(employee);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(baseUrl + "/employee"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                log.info("Successfully added employee: {}", employee.getName());
                return objectMapper.readValue(response.body(), AddResponse.class).getData();
            }

            log.error("Failed to add employee: {}. Status: {}", employee.getName(), response.statusCode());
            throw new ApiException("Failed to add employee", response.statusCode());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while adding employee: {}", employee.getName(), e);
            throw new ApiException("Request interrupted", 0, e);
        } catch (IOException e) {
            log.error("IO error adding employee: {}", employee.getName(), e);
            throw new ApiException("Network error occurred", 0, e);
        }
    }
    public String deleteEmployeeById(String id) throws ApiException {
        DeleteDto dto = new DeleteDto();
        dto.setName(id);
        String requestBody = toJson(dto);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(java.net.URI.create(baseUrl + "/employee"))
                .method("DELETE", HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (HttpStatus.valueOf(response.statusCode()).is2xxSuccessful()) {
                try {
                    DeleteResponse deleteResponse = new DeleteResponse();
                    deleteResponse = objectMapper.readValue(response.body(), DeleteResponse.class);
                    if (deleteResponse.isData()) {
                        log.info("Successfully deleted employee with id: {}", id);
                    } else {
                        //TODO : What do you want to do if employee not found? but request was successful?
                        log.warn("Employee with id: {} not found", id);
                        return null;
                    }
                    return id;
                } catch (JsonProcessingException e) {
                    log.error("Failed to parse delete response", e);
                    throw new ApiException("Invalid response format", response.statusCode());
                }
            }

            log.error("Failed to delete employee. Status: {}", response.statusCode());
            throw new ApiException("Failed to delete employee", response.statusCode());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while deleting employee with id: {}", id, e);
            throw new ApiException("Request interrupted", 0, e);
        } catch (IOException e) {
            log.error("IO error deleting employee with id: {}", id, e);
            throw new ApiException("Network error occurred", 0, e);
        }
    }
    private String toJson(Object obj) throws ApiException {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Error serializing object to JSON", e);
            throw new ApiException("Failed to serialize request", 0, e);
        }
    }
}
