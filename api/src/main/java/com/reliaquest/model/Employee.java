package com.reliaquest.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Employee(
        String id,
        String employeeName,
        Integer employeeSalary,
        Integer employeeAge,
        String employeeTitle,
        String employeeEmail) {}
