package com.bagas.springrestapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RegisterDepartmentRequest {

    @NotBlank
    @Size(max = 4)
    private String deptNo;

    @NotBlank
    @Size(max = 40)
    private String deptName;
}
