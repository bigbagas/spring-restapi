package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeesRepository extends JpaRepository<Employee,Integer>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findFirstByFirstName(String firstName);
}
