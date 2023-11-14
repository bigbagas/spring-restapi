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

    @Temporal(TemporalType.DATE)
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",message = "format must yyyy-MM-dd")
    private String birthDate;

    @Size(max = 14,message = "length must be between 0 and 14")
    private String firstName;

    @Size(max = 16,message = "length must be between 0 and 16")
    private String lastName;

    @Size(max = 1,message = "must either M or F")
    @Pattern(regexp = "[MFmf]+",message = "must either M or F")
    private String gender;

    @Temporal(TemporalType.DATE)
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",message = "format must yyyy-MM-dd")
    private String hireDate;


}
