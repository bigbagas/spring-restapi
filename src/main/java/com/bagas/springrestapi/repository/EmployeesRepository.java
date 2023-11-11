package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee,Integer> {
}
