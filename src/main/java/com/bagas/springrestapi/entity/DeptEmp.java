package com.bagas.springrestapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dept_emps")
public class DeptEmp extends RepresentationModel<DeptEmp> {


    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    private Date toDate;


//    private String deptNo;
//
    @Id
    private Integer empNo;

    @ManyToOne
    @JoinColumn(name = "dept_no",referencedColumnName = "dept_no")
    private Department department;


    @OneToOne
    @MapsId
    @JoinColumn(name = "emp_no",referencedColumnName = "emp_no")
    private Employee employee;
}
