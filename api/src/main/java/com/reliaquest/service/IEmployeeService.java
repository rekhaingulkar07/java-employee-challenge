package com.reliaquest.service;

import com.reliaquest.model.Employee;
import com.reliaquest.model.EmployeeRequest;
import java.util.List;

public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    int getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(EmployeeRequest employeeRequest);

    String deleteEmployeeById(String id);
}
