package com.bagas.springrestapi.model;

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

    @Size(max = 4)
    private String deptNo;

    @Size(max = 40)
    private String deptName;
}
