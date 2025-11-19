package com.reliaquest.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeRequest {
    @NotBlank
    private String name;

    @NotNull private Integer salary;

    @NotNull @Min(16)
    @Max(75)
    private Integer age;

    @NotBlank
    private String title;
}
