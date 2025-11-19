package com.reliaquest.model;

import java.util.List;
import lombok.Data;

@Data
public class EmployeeListResponse {
    private String status;
    private List<Employee> data;
}
