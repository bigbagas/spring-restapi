package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.DeptEmp;
import com.bagas.springrestapi.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeptEmpRepository extends JpaRepository<DeptEmp, Employee> {

    Optional<DeptEmp> findFirstByEmpNoAndDepartment_DeptNo(Integer empNo, String deptNo);
//
//    Page<DeptEmp> findByDeptNo(String deptNo, Pageable pageable);
//
//    DeptEmp findByDeptNoAndEmpNo( String deptNo,Integer empNo);

}
