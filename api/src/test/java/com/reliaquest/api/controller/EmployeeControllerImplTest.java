package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.reliaquest.model.Employee;
import com.reliaquest.model.EmployeeRequest;
import com.reliaquest.service.EmployeeServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EmployeeControllerImplTest {

    @Mock
    private EmployeeServiceImpl employeeServiceImpl;

    @InjectMocks
    private EmployeeControllerImpl employeeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> mockEmployees = Arrays.asList(
                new Employee(null, null, null, null, null, null), new Employee(null, null, null, null, null, null));
        when(employeeServiceImpl.getAllEmployees()).thenReturn(mockEmployees);
        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployees, response.getBody());
    }

    @Test
    void testGetEmployeesByNameSearch() {
        String searchString = "John";
        List<Employee> mockEmployees = Arrays.asList(new Employee(null, null, null, null, null, null));
        when(employeeServiceImpl.getEmployeesByNameSearch(searchString)).thenReturn(mockEmployees);
        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployees, response.getBody());
    }

    @Test
    void testGetEmployeeById() {
        String id = "123";
        Employee mockEmployee = new Employee(null, null, null, null, null, null);
        when(employeeServiceImpl.getEmployeeById(id)).thenReturn(mockEmployee);
        ResponseEntity<Employee> response = employeeController.getEmployeeById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockEmployee, response.getBody());
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        int highestSalary = 100000;
        when(employeeServiceImpl.getHighestSalaryOfEmployees()).thenReturn(highestSalary);
        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(highestSalary, response.getBody());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames() {
        List<String> names = Arrays.asList("Alice", "Bob");
        when(employeeServiceImpl.getTopTenHighestEarningEmployeeNames()).thenReturn(names);
        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(names, response.getBody());
    }

    @Test
    void testCreateEmployee() {
        EmployeeRequest request = new EmployeeRequest();
        Employee createdEmployee = new Employee(null, null, null, null, null, null);
        when(employeeServiceImpl.createEmployee(request)).thenReturn(createdEmployee);
        ResponseEntity<Employee> response = employeeController.createEmployee(request);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(createdEmployee, response.getBody());
    }

    @Test
    void testDeleteEmployeeById() {
        String id = "123";
        String deletedName = "John Doe";
        when(employeeServiceImpl.deleteEmployeeById(id)).thenReturn(deletedName);
        ResponseEntity<String> response = employeeController.deleteEmployeeById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(deletedName, response.getBody());
    }
}
