package com.reliaquest.model;

import lombok.Data;

@Data
public class EmployeeResponse {
    private String status;
    private Employee data;
}
