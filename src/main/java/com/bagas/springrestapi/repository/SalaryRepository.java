package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary,Integer> {

    @Query(
            value = "select * from salaries s where s.emp_no = :empNo",
            nativeQuery = true
    )
    Optional<Salary> salaryByEmpNo(Integer empNo);


    @Modifying
    @Query(
            value = "call insert_salary(:empNo, :salary,:fromDate, :toDate)",
            nativeQuery = true
    )
    public void insertIntoSalary(Integer empNo, Integer salary, Date fromDate, Date toDate);
}
