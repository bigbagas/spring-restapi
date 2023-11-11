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
@Table(name = "dept_managers")
public class DeptManager {

    @Id
    private Date fromDate;

    private Date toDate;

    @ManyToOne
    @JoinColumn(name = "dept_no",referencedColumnName = "dept_no")
    private Department department;

    @OneToOne
    @JoinColumn(name = "emp_no",referencedColumnName = "emp_no")
    private Employee employee;

}
