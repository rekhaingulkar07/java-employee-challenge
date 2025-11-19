package com.reliaquest.api.controller;

import com.reliaquest.model.Employee;
import com.reliaquest.model.EmployeeRequest;
import com.reliaquest.service.EmployeeServiceImpl;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeControllerImpl implements IEmployeeController<Employee, EmployeeRequest> {

    private EmployeeServiceImpl employeeServiceImpl;

    public EmployeeControllerImpl(EmployeeServiceImpl employeeServiceImpl) {
        this.employeeServiceImpl = employeeServiceImpl;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Inside EmployeeControllerImpl.getAllEmployees()");
        List<Employee> employeeList = new ArrayList<>();
        employeeList = employeeServiceImpl.getAllEmployees();
        return new ResponseEntity<List<Employee>>(employeeList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable("searchString") String searchString) {
        log.info("Inside EmployeeControllerImpl.getEmployeesByNameSearch()");
        List<Employee> employeeList = new ArrayList<>();
        employeeList = employeeServiceImpl.getEmployeesByNameSearch(searchString);
        return new ResponseEntity<List<Employee>>(employeeList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") String id) {
        log.info("Inside EmployeeControllerImpl.getEmployeeById()");
        Employee employee = new Employee(null, null, null, null, null, null);
        employee = employeeServiceImpl.getEmployeeById(id);
        return new ResponseEntity<Employee>(employee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        log.info("Inside EmployeeControllerImpl.getHighestSalaryOfEmployees()");
        int highestSalary = 0;
        highestSalary = employeeServiceImpl.getHighestSalaryOfEmployees();
        return new ResponseEntity<Integer>(highestSalary, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        log.info("Inside EmployeeControllerImpl.getTopTenHighestEarningEmployeeNames()");
        List<String> highestEarningEmployeeNames = new ArrayList<>();
        highestEarningEmployeeNames = employeeServiceImpl.getTopTenHighestEarningEmployeeNames();
        return new ResponseEntity<List<String>>(highestEarningEmployeeNames, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid EmployeeRequest employeeRequest) {
        log.info("Inside EmployeeControllerImpl.createEmployee()");
        Employee employee = new Employee(null, null, null, null, null, null);
        employee = employeeServiceImpl.createEmployee(employeeRequest);
        return new ResponseEntity<Employee>(employee, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        log.info("Inside EmployeeControllerImpl.deleteEmployeeById()");
        String employeeName = null;
        employeeName = employeeServiceImpl.deleteEmployeeById(id);
        return new ResponseEntity<String>(employeeName, HttpStatus.OK);
    }
}
