package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.DeptManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeptManagerRepository extends JpaRepository<DeptManager,Integer> {

    Optional<DeptManager> findByDepartment_DeptNoAndAndEmpNo(String deptNo, Integer empNo);

    Page<DeptManager> findByDepartment_DeptNo(String deptNo, Pageable pageable);

    List<DeptManager> findAllByDepartment_DeptNo(String deptNo);

}
