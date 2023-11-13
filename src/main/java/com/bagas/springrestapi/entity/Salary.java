package com.bagas.springrestapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    private Integer empNo;

    private Integer salary;

    private Date fromDate;

    private Date toDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "emp_no",referencedColumnName = "emp_no")
    private Employee employee;


}
