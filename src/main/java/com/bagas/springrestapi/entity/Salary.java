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
    @Column(length = 11)
    private Integer empNo;

    @Column(length = 11)
    private Integer salary;

    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    private Date toDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "emp_no",referencedColumnName = "emp_no")
    private Employee employee;


}
