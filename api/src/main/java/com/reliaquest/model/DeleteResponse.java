package com.reliaquest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteResponse<T> {
    private T data;
    private boolean success;
    private String message;
}
