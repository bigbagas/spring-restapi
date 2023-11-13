package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends JpaRepository<Salary,Integer> {
}
