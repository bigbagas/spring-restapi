package com.bagas.springrestapi.model;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DeptEmpResponse extends RepresentationModel<DeptEmpResponse> {

    private String deptNo;

    private Integer empNo;

    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    private Date toDate;
}
