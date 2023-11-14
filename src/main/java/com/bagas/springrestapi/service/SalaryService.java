package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.model.RegisterSalaryRequest;
import com.bagas.springrestapi.model.SalaryResponse;
import com.bagas.springrestapi.model.UpdateSalaryRequest;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.bagas.springrestapi.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
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

//        Optional<Salary> salary = Optional.ofNullable(salaryRepository.salaryByEmpNo(empNo)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee salary is not found")));

        Salary salary = salaryRepository.salaryByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Salary is not found"));

        return toSalaryResponse(salary);

    }

    @Transactional(readOnly = true)
    public Page<SalaryResponse> getAllSalary(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("emp_no").ascending());
        Page<Salary> salaries = salaryRepository.allSalaryWithPageable(pageable);
        List<SalaryResponse> salaryResponseList = salaries.getContent().stream()
                .map(this::toSalaryResponse).toList();
        return new PageImpl<>(salaryResponseList,pageable,salaries.getTotalElements());
    }

    private SalaryResponse toSalaryResponse (Salary salary){
        return SalaryResponse.builder()
                .empNo(salary.getEmpNo())
                .salary(salary.getSalary())
                .fromDate(salary.getFromDate())
                .toDate(salary.getToDate())
                .build();
    }

    @Transactional
    public SalaryResponse updateSalary(Integer empNo, UpdateSalaryRequest request){
        validationService.validate(request);

        Employee employee = employeeRepository.employeeByEmpNo(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Salary salary = salaryRepository.salaryByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Salary is not found"));

        Integer newSalary = request.getSalary();
        Date newFromDate = request.getFromDate();
        Date newToDate = request.getToDate();

        if (Objects.isNull(request.getSalary())){
            newSalary = salary.getSalary();
        }

        if (Objects.isNull(request.getFromDate())){
            newFromDate = salary.getFromDate();
        }

        if (Objects.isNull(request.getToDate())){
            newToDate = salary.getToDate();
        }

        salaryRepository.updateSalary(newSalary,empNo,newFromDate,newToDate);

        Salary updatedSalary = new Salary();
        updatedSalary.setEmpNo(empNo);
        updatedSalary.setSalary(newSalary);
        updatedSalary.setFromDate(newFromDate);
        updatedSalary.setToDate(newToDate);
        System.out.println(updatedSalary.getSalary());

        return toSalaryResponse(updatedSalary);

    }

    @Transactional
    public void deleteSalary(Integer empNo){

        Employee employee = employeeRepository.employeeByEmpNo(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Salary salary = salaryRepository.salaryByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee Salary is not found"));

        salaryRepository.deleteSalary(empNo);


    }

    @Transactional(readOnly = true)
    public Page<SalaryResponse> searchSalary(Integer keyword, Integer page, Integer size ){
        Pageable pageable = PageRequest.of(page,size,Sort.by("emp_no").ascending());

        Page<Salary> salaries = salaryRepository.allSalaryWithPageable(pageable);
        if (Objects.nonNull(keyword)){
            salaries = salaryRepository.searchSalary(keyword,pageable);
        }
        List<SalaryResponse> salaryResponseList = salaries.getContent().stream()
                .map(this::toSalaryResponse).toList();
        return new PageImpl<>(salaryResponseList,pageable,salaries.getTotalElements());


    }
}
