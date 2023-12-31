package com.bagas.springrestapi.model;

import com.bagas.springrestapi.entity.Salary;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSalaryResponse {

    private Integer empNo;

    @Temporal(TemporalType.DATE)
    private Date birthDate;

    private String firstName;

    private String lastName;

    private String gender;

    @Temporal(TemporalType.DATE)
    private Date hireDate;

    private SalaryResponse salary;

}
