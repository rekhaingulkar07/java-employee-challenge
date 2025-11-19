package com.reliaquest.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.reliaquest.constants.Constants;
import com.reliaquest.exceptions.*;
import com.reliaquest.model.*;
import java.util.Arrays;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

class EmployeeServiceImplTest {
    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeServiceImpl(webClient);
    }

    private Employee createEmployee() {
        // Use all fields as per your Employee record
        return new Employee("1", "John", 1000, 30, "Engineer", "john@email.com");
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesSuccess() {
        Employee emp = createEmployee();
        EmployeeListResponse resp = new EmployeeListResponse();
        resp.setData(Arrays.asList(emp));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.just(resp));
        List<Employee> result = employeeService.getAllEmployees();
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).employeeName());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesNotFound() {
        EmployeeListResponse resp = new EmployeeListResponse();
        resp.setData(null);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.just(resp));
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getAllEmployees());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetEmployeesByNameSearchFound() {
        Employee emp = createEmployee();
        EmployeeListResponse resp = new EmployeeListResponse();
        resp.setData(Arrays.asList(emp));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.just(resp));
        List<Employee> result = employeeService.getEmployeesByNameSearch("John");
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).employeeName());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetEmployeesByNameSearchNotFound() {
        Employee emp = createEmployee();
        EmployeeListResponse resp = new EmployeeListResponse();
        resp.setData(Arrays.asList(emp));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.just(resp));
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeesByNameSearch("Jane"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetEmployeeByIdSuccess() {
        Employee emp = createEmployee();
        EmployeeResponse resp = new EmployeeResponse();
        resp.setData(emp);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_EMPLOYEE_BY_ID, "1"))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(resp));
        Employee result = employeeService.getEmployeeById("1");
        assertEquals("John", result.employeeName());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetEmployeeByIdNotFound() {
        EmployeeResponse resp = new EmployeeResponse();
        resp.setData(null);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_EMPLOYEE_BY_ID, "2"))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(resp));
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById("2"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetHighestSalaryOfEmployees() {
        Employee emp1 = new Employee("1", "John", 1000, 30, "Engineer", "john@email.com");
        Employee emp2 = new Employee("2", "Jane", 2000, 28, "Manager", "jane@email.com");
        EmployeeListResponse resp = new EmployeeListResponse();
        resp.setData(Arrays.asList(emp1, emp2));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.just(resp));
        int highest = employeeService.getHighestSalaryOfEmployees();
        assertEquals(2000, highest);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        Employee emp1 = new Employee("1", "John", 1000, 30, "Engineer", "john@email.com");
        Employee emp2 = new Employee("2", "Jane", 2000, 28, "Manager", "jane@email.com");
        EmployeeListResponse resp = new EmployeeListResponse();
        resp.setData(Arrays.asList(emp1, emp2));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.just(resp));
        List<String> names = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(Arrays.asList("Jane", "John"), names);
    }

    // Exception code paths for WebClient errors
    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesWebClientNotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        WebClientResponseException ex = new WebClientResponseException("Not Found", 404, "Not Found", null, null, null);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.error(ex));
        Throwable thrown = assertThrows(Exception.class, () -> employeeService.getAllEmployees());
        assertTrue(thrown.getCause() instanceof BadRequestException);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesWebClientTooManyRequests() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        WebClientResponseException ex =
                new WebClientResponseException("Too Many Requests", 429, "Too Many Requests", null, null, null);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.error(ex));
        assertThrows(RetryFailedException.class, () -> employeeService.getAllEmployees());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesWebClientClientError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        WebClientResponseException ex =
                new WebClientResponseException("Bad Request", 400, "Bad Request", null, null, null);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.error(ex));
        Throwable thrown = assertThrows(Exception.class, () -> employeeService.getAllEmployees());
        assertTrue(thrown.getCause() instanceof BadRequestException);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesWebClientServerError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        WebClientResponseException ex =
                new WebClientResponseException("Server Error", 500, "Server Error", null, null, null);
        when(responseSpec.bodyToMono(EmployeeListResponse.class)).thenReturn(Mono.error(ex));
        assertThrows(RetryFailedException.class, () -> employeeService.getAllEmployees());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesWebClientDecodingError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class))
                .thenReturn(Mono.error(new org.springframework.core.codec.DecodingException("Decoding error")));
        assertThrows(InvalidResponseException.class, () -> employeeService.getAllEmployees());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetAllEmployeesWebClientUnexpectedError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(Constants.REST_API_URI_GET_ALL_EMPLOYEES))
                .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmployeeListResponse.class))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));
        assertThrows(RuntimeException.class, () -> employeeService.getAllEmployees());
    }
}
