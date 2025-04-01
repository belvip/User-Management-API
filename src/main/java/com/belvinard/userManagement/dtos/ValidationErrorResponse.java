package com.belvinard.userManagement.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private String code;
    private List<String> errors;
}
