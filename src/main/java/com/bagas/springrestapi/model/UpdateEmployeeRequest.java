package com.bagas.springrestapi.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEmployeeRequest {

    @Past

    private Date birthDate;

    @Size(max = 14)
    private String firstName;

    @Size(max = 16)
    private String lastName;

    @Size(max = 1)
    @Pattern(regexp = "[MFmf]+", message = "Gender must either M or F")
    private String gender;

    private Date hireDate;


}
