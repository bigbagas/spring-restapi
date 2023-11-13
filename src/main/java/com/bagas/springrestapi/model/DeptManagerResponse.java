package com.bagas.springrestapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DeptManagerResponse {

    private String deptNo;

    private Integer empNo;

    private Date fromDate;

    private Date toDate;

}
