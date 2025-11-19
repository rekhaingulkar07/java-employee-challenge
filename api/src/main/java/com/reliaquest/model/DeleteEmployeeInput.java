package com.reliaquest.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteEmployeeInput {

    @NotBlank
    private String name;
}
