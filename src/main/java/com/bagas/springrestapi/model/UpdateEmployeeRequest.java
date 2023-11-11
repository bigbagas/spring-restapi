package com.bagas.springrestapi.model;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Size(max = 14,message = "length must be between 0 and 14")
    private String firstName;

    @Size(max = 16,message = "length must be between 0 and 16")
    private String lastName;

    @Size(max = 1,message = "must either M or F")
    @Pattern(regexp = "[MFmf]+",message = "must either M or F")
    private String gender;

    @Temporal(TemporalType.DATE)
    private Date hireDate;


}
