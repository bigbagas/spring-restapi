package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Integer>, JpaSpecificationExecutor<Employee> {

    Optional<Employee> findFirstByFirstName(String firstName);

    Optional<Employee> findByBirthDateAndAndFirstNameAndLastNameAndHireDateAndGender(Date birthDate, String firstName, String lastName, Date hireDate,String gender);

    @Query(
            value = "select * from employees e where e.emp_no = :empNo",
            nativeQuery = true
    )
    Optional<Employee> employeeByEmpNo (Integer empNo);


}
