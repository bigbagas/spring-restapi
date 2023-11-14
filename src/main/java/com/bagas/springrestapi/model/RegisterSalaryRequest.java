package com.bagas.springrestapi.model;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterSalaryRequest {

    @NotNull
    @Column(length = 11)
    private Integer empNo;

    @NotNull
    @Column(length = 11)
    private Integer salary;

    @NotNull
    private Date fromDate;

    @NotNull
    private Date toDate;
}
