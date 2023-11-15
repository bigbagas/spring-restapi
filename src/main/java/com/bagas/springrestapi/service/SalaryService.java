package com.bagas.springrestapi.service;

import com.bagas.springrestapi.controller.EmployeeController;
import com.bagas.springrestapi.controller.SalaryController;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.model.RegisterSalaryRequest;
import com.bagas.springrestapi.model.SalaryResponse;
import com.bagas.springrestapi.model.UpdateSalaryRequest;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.bagas.springrestapi.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void registerSalary(RegisterSalaryRequest request) throws ParseException {
        validationService.validate(request);
        validationService.dateValidation(request.getFromDate(),request.getToDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = employeeRepository.employeeByEmpNo(request.getEmpNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Optional<Salary> salary = salaryRepository.salaryByEmpNo(request.getEmpNo());
        if (salary.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee already have Salary");
        }

        salaryRepository.insertIntoSalary(request.getEmpNo(), request.getSalary(), sdf.parse(request.getFromDate()),sdf.parse(request.getToDate()));
    }

    @Transactional(readOnly = true)
    public SalaryResponse getSalaryByEmpNo(Integer empNo){
      Employee employee = employeeRepository.employeeByEmpNo(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Salary salary = salaryRepository.salaryByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Salary is not found"));

        addLink(salary);

        return toSalaryResponse(salary);
    }

    @Transactional(readOnly = true)
    public Page<SalaryResponse> getAllSalary(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("emp_no").ascending());
        Page<Salary> salaries = salaryRepository.allSalaryWithPageable(pageable);

        List<Salary> salaryList = salaries.getContent();
        for (int i = 0; i < salaryList.size(); i++) {
            Salary salary = salaryList.get(i);
            addLink(salary);
        }

        List<SalaryResponse> salaryResponseList = salaries.getContent().stream()
                .map(this::toSalaryResponse).toList();
        return new PageImpl<>(salaryResponseList,pageable,salaries.getTotalElements());
    }

    private SalaryResponse toSalaryResponse (Salary salary){
        SalaryResponse salaryResponse = SalaryResponse.builder()
                .empNo(salary.getEmpNo())
                .salary(salary.getSalary())
                .fromDate(salary.getFromDate())
                .toDate(salary.getToDate())
                .build();
        salaryResponse.add(salary.getLinks());
        return salaryResponse;
    }

    @Transactional
    public SalaryResponse updateSalary(Integer empNo, UpdateSalaryRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = employeeRepository.employeeByEmpNo(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Salary salary = salaryRepository.salaryByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Salary is not found"));

        Integer newSalary = request.getSalary();
        Date newFromDate = sdf.parse(request.getFromDate());
        Date newToDate = sdf.parse(request.getToDate());

        String checkFromDate = request.getFromDate();
        String checkToDate = request.getToDate();

        if (Objects.isNull(request.getSalary())){
            newSalary = salary.getSalary();
        }

        if (Objects.isNull(request.getFromDate())){
            checkFromDate = sdf.format(salary.getFromDate());
            newFromDate = salary.getFromDate();
        }

        if (Objects.isNull(request.getToDate())){
            checkToDate = sdf.format(salary.getToDate());
            newToDate = salary.getToDate();
        }

        validationService.dateValidation(checkFromDate,checkToDate);

        salaryRepository.updateSalary(newSalary,empNo,newFromDate,newToDate);

        Salary updatedSalary = new Salary();
        updatedSalary.setEmpNo(empNo);
        updatedSalary.setSalary(newSalary);
        updatedSalary.setFromDate(newFromDate);
        updatedSalary.setToDate(newToDate);
        addLink(updatedSalary);
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

        Page<Salary> salaries;
        if (Objects.nonNull(keyword)){
            salaries = salaryRepository.searchSalary(keyword,pageable);
        }else {
            salaries = salaryRepository.allSalaryWithPageable(pageable);
        }
        List<Salary> salaryList = salaries.getContent();
        for (int i = 0; i < salaryList.size(); i++) {
            Salary salary = salaryList.get(i);
            addLink(salary);
        }

        List<SalaryResponse> salaryResponseList = salaries.getContent().stream()
                .map(this::toSalaryResponse).toList();
        return new PageImpl<>(salaryResponseList,pageable,salaries.getTotalElements());


    }

    private void addLink(Salary salary){
        Link link = linkTo(methodOn(SalaryController.class).getSalaryByEmpNo(salary.getEmpNo())).withSelfRel();
        salary.add(link);

        if (employeeRepository.findById(salary.getEmpNo()).isPresent()){
            Link linkEmployee = linkTo(methodOn(EmployeeController.class).getEmployeeByEmpNo(salary.getEmpNo())).withRel("employee");
            salary.add(linkEmployee);
        }
    }
}
