package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.DeptEmp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeptEmpRepository extends JpaRepository<DeptEmp, Date> {

    Optional<DeptEmp> findFirstByEmpNoAndDeptNo(Integer empNo, String deptNo);

    Page<DeptEmp> findByDeptNo(String deptNo, Pageable pageable);

}
