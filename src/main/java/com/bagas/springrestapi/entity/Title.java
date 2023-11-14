package com.bagas.springrestapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "titles")
public class Title {

    @Id
    private Integer empNo;

    @Column(length = 40)
    private String title;

    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    private Date toDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "emp_no",referencedColumnName = "emp_no")
    private Employee employee;

}
