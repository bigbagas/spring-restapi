package com.bagas.springrestapi.entity;

import com.bagas.springrestapi.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "emp_no",length = 11)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empNo;

    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @Column(length = 14)
    private String firstName;

    @Column(length = 16)
    private String lastName;

    @Column(length = 1)
    private String gender;

    @Temporal(TemporalType.DATE)
    private Date hireDate;

    @OneToOne(mappedBy = "employee")
    private DeptEmp deptEmps;

    @OneToOne(mappedBy = "employee")
    private DeptManager deptManager;

    @OneToOne(mappedBy = "employee")
    private Salary salary;

    @OneToOne(mappedBy = "employee")
    private Title title;




}
