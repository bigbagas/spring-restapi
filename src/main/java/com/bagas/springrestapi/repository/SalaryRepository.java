package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
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
    void insertIntoSalary(Integer empNo, Integer salary, Date fromDate, Date toDate);

    @Query(
            value = "select * from salaries order by salaries.emp_no",
            countQuery = "select count(*) from salaries",
            nativeQuery = true
    )
    Page<Salary> allSalaryWithPageable(Pageable pageable);

    @Modifying
    @Query(
            value = "call update_salary( :salary,:empNo,:fromDate, :toDate)",
            nativeQuery = true
    )
    void updateSalary(Integer salary, Integer empNo, Date fromDate, Date toDate);

    @Modifying
    @Query(
            value = "call delete_salary(:empNo)",
            nativeQuery = true
    )
    void deleteSalary(Integer empNo);

    @Query(
            value = "select * from salaries where salary = :salary order by salaries.emp_no",
            countQuery = "select count(*) from salaries",
            nativeQuery = true
    )
    Page<Salary> searchSalary(Integer salary,Pageable pageable);
}
