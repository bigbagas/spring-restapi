package com.bagas.springrestapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RegisterDeptEmpRequest {

    @NotNull
    @Column(length = 11)
    private Integer empNo;

    @NotBlank
    @Size(max = 4)
    private String deptNo;

    @NotBlank
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",message = "date format must yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private String fromDate;

    @NotBlank
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",message = "date format must yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private String toDate;

}
