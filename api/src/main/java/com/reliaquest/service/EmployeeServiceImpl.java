package com.reliaquest.service;

import com.reliaquest.constants.Constants;
import com.reliaquest.exceptions.EmployeeNotFoundException;
import com.reliaquest.exceptions.InvalidResponseException;
import com.reliaquest.exceptions.RemoteServiceException;
import com.reliaquest.exceptions.RetryFailedException;
import com.reliaquest.model.DeleteEmployeeInput;
import com.reliaquest.model.DeleteResponse;
import com.reliaquest.model.Employee;
import com.reliaquest.model.EmployeeListResponse;
import com.reliaquest.model.EmployeeRequest;
import com.reliaquest.model.EmployeeResponse;
import com.reliaquest.util.RetryPolicy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class EmployeeServiceImpl implements IEmployeeService {

    private WebClient webClient;

    public EmployeeServiceImpl(@Qualifier("employeeWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Getting all employees list");
        // NOTE: .block() used because mock server is synchronous.
        // In real production code we would avoid blocking.
        EmployeeListResponse employeeListResponse = webClient
                .get()
                .uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES)
                .retrieve()
                .bodyToMono(EmployeeListResponse.class)
                .retryWhen(RetryPolicy.retrySpec()) // RETRY FIRST
                .transform(mono -> handleWebClientErrors(mono, "No employees found"))
                .block();

        if (employeeListResponse == null || employeeListResponse.getData() == null) {
            throw new EmployeeNotFoundException("No employees found.");
        }
        log.info(
                "Successfully fetched all {} employees",
                employeeListResponse.getData().size());
        return employeeListResponse.getData();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        log.info("Getting employees by name:{}", searchString);
        List<Employee> employeeList = new ArrayList<>();
        String search = searchString.toLowerCase();
        employeeList = getAllEmployees().stream()
                .filter(e -> e.employeeName() != null
                        && (e.employeeName().equalsIgnoreCase(searchString)
                                || e.employeeName().toLowerCase().contains(search)))
                .toList();
        if (!employeeList.isEmpty()) {
            log.info("Found {} employees by name {}", employeeList.size(), searchString);
            return employeeList;
        } else {
            log.info("Employees not found by name:{}", searchString);
            throw new EmployeeNotFoundException("Employees not found by name: " + search);
        }
    }

    @Override
    public Employee getEmployeeById(String id) {
        log.info("Getting employee by id:{}", id);
        EmployeeResponse employeeResponse = webClient
                .get()
                .uri(Constants.REST_API_URI_GET_EMPLOYEE_BY_ID, id)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .retryWhen(RetryPolicy.retrySpec()) // RETRY FIRST
                .transform(mono -> handleWebClientErrors(mono, "Employee not found for id: " + id))
                .block();

        if (employeeResponse == null || employeeResponse.getData() == null) {
            throw new EmployeeNotFoundException("Employee not found for id: " + id);
        }
        log.info("Employee found succesffully by id:{}", id);
        return employeeResponse.getData();
    }

    @Override
    public int getHighestSalaryOfEmployees() {
        log.info("Getting highest salary of employees");
        int highestSalary = getAllEmployees().stream()
                .mapToInt(Employee::employeeSalary)
                .max()
                .orElse(0);
        log.info("Highest salary of employee:{}", highestSalary);
        return highestSalary;
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        log.info("Getting top ten highest earning employee names");
        List<String> topTenHighestEarningEmployeeNames = getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::employeeSalary).reversed())
                .limit(10)
                .map(Employee::employeeName)
                .toList();
        log.info("Top ten higest earning employees name are:{}", topTenHighestEarningEmployeeNames.toString());
        return topTenHighestEarningEmployeeNames;
    }

    @Override
    public Employee createEmployee(EmployeeRequest employeeRequest) {
        log.info("Creating new employee");
        EmployeeResponse employeeResponse = webClient
                .post()
                .uri(Constants.REST_API_URI_CREATE_DELETE_EMPLOYEE)
                .bodyValue(employeeRequest)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .retryWhen(RetryPolicy.retrySpec()) // RETRY FIRST
                .transform(mono -> handleWebClientErrors(mono, "Failed to create employee"))
                .block();
        if (employeeResponse == null || employeeResponse.getData() == null) {
            throw new RuntimeException("Failed to create employee: empty response");
        }
        log.info(
                "New employee created successfully with id:{}",
                employeeResponse.getData().id());
        return employeeResponse.getData();
    }

    @Override
    public String deleteEmployeeById(String id) {
        log.info("Deleting employee by id:{}", id);
        DeleteEmployeeInput deleteEmployeeInput = new DeleteEmployeeInput();
        Employee employee = getEmployeeById(id);
        if (employee != null) {
            deleteEmployeeInput.setName(employee.employeeName());
        }

        DeleteResponse<Boolean> deleted = webClient
                .method(HttpMethod.DELETE)
                .uri(Constants.REST_API_URI_CREATE_DELETE_EMPLOYEE)
                .bodyValue(deleteEmployeeInput)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<DeleteResponse<Boolean>>() {})
                .retryWhen(RetryPolicy.retrySpec())
                .transform(mono -> handleWebClientErrors(mono, "Failed to delete employee"))
                .block();

        if (deleted.getData()) {
            log.info("Successfully deleted employee by id:{}", id);
            return employee.employeeName();
        } else {
            throw new RuntimeException("Failed to delete employee by id: " + id);
        }
    }

    private <T> Mono<T> handleWebClientErrors(Mono<T> mono, String notFoundMessage) {
        return mono
                // Convert 404 → EmployeeNotFoundException
                .onErrorMap(
                        WebClientResponseException.NotFound.class, ex -> new EmployeeNotFoundException(notFoundMessage))

                // Convert other 429 → BadRequestException
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS)) {
                        return new RetryFailedException("Retry attempts exhausted", new Throwable());
                    }
                    return ex;
                })

                // Convert other 4xx → BadRequestException
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().is4xxClientError()) {
                        return new BadRequestException("Client error: " + ex.getStatusCode());
                    }
                    return ex;
                })

                // Convert 5xx → RemoteServiceException
                .onErrorMap(WebClientResponseException.class, ex -> {
                    if (ex.getStatusCode().is5xxServerError()) {
                        return new RemoteServiceException("Remote server error: " + ex.getStatusCode());
                    }
                    return ex;
                })

                // JSON Decoding → InvalidResponseException
                .onErrorMap(DecodingException.class, ex -> new InvalidResponseException("Invalid response format"))

                // Catch-all ONLY if it's not already our custom exception
                .onErrorMap(
                        ex -> !(ex instanceof EmployeeNotFoundException
                                || ex instanceof BadRequestException
                                || ex instanceof RemoteServiceException
                                || ex instanceof InvalidResponseException
                                || ex instanceof RetryFailedException),
                        ex -> new RuntimeException("Unexpected error"));
    }
}
