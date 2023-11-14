package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.model.RegisterSalaryRequest;
import com.bagas.springrestapi.model.SalaryResponse;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.bagas.springrestapi.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void registerSalary(RegisterSalaryRequest request){
        validationService.validate(request);

        Employee employee = employeeRepository.employeeByEmpNo(request.getEmpNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Optional<Salary> salary = salaryRepository.salaryByEmpNo(request.getEmpNo());
        if (salary.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee already have Salary");
        }

        salaryRepository.insertIntoSalary(request.getEmpNo(), request.getSalary(), request.getFromDate(),request.getToDate());



    }

    @Transactional(readOnly = true)
    public SalaryResponse getSalaryByEmpNo(Integer empNo){

        Optional<Salary> salary = Optional.ofNullable(salaryRepository.salaryByEmpNo(empNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee salary is not found")));

        return toSalaryResponse(salary.get());


    }

    private SalaryResponse toSalaryResponse (Salary salary){
        return SalaryResponse.builder()
                .empNo(salary.getEmpNo())
                .salary(salary.getSalary())
                .fromDate(salary.getFromDate())
                .toDate(salary.getToDate())
                .build();
    }
}
