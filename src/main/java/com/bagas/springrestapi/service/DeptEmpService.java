package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptEmp;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.DeptEmpResponse;
import com.bagas.springrestapi.model.RegisterDeptEmpRequest;
import com.bagas.springrestapi.repository.DepartmentRepository;
import com.bagas.springrestapi.repository.DeptEmpRepository;
import com.bagas.springrestapi.repository.EmployeeRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DeptEmpService {

    @Autowired
    private DeptEmpRepository deptEmpRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void registerDeptEmp (RegisterDeptEmpRequest request){
        validationService.validate(request);
        System.out.println(request);

        Optional<DeptEmp> deptEmpCheck = deptEmpRepository.findFirstByEmpNoAndDepartment_DeptNo(request.getEmpNo(),request.getDeptNo());

        if (deptEmpCheck.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee is already register is this department");
        }

        Department department = departmentRepository.findById(request.getDeptNo())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(request.getEmpNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptEmp deptEmp = new DeptEmp();
        deptEmp.setFromDate(request.getFromDate());
        deptEmp.setToDate(request.getToDate());
//        deptEmp.setEmpNo(employee.getEmpNo());
//        deptEmp.setDeptNo(department.getDeptNo());
        deptEmp.setDepartment(department);
        deptEmp.setEmployee(employee);

        deptEmpRepository.save(deptEmp);
    }

    @Transactional(readOnly = true)
    public Page<DeptEmpResponse> deptEmpAllEmployeeByDeptNo(String deptNo, Integer page, Integer size){

        Pageable pageable = PageRequest.of(page,size, Sort.by("empNo").ascending());
        Page<DeptEmp> deptEmps = deptEmpRepository.findByDepartment_DeptNo(deptNo,pageable);
        List<DeptEmpResponse> deptEmpResponseList = deptEmps.stream()
                .map(this::toDeptEmpResponse).toList();

        return new PageImpl<>(deptEmpResponseList,pageable,deptEmps.getTotalElements());
    }

    private DeptEmpResponse toDeptEmpResponse(DeptEmp deptEmp){
        return DeptEmpResponse.builder()
                .deptNo(deptEmp.getDepartment().getDeptNo())
                .empNo(deptEmp.getEmpNo())
                .fromDate(deptEmp.getFromDate())
                .toDate(deptEmp.getToDate())
                .build();
    }
//
    @Transactional(readOnly = true)
    public Page<DeptEmpResponse> deptEmpAllEmployee( Integer page, Integer size){

        Pageable pageable = PageRequest.of(page,size, Sort.by("empNo").ascending());
        Page<DeptEmp> deptEmps = deptEmpRepository.findAll(pageable);
        List<DeptEmpResponse> deptEmpResponseList = deptEmps.stream()
                .map(this::toDeptEmpResponse).toList();

        return new PageImpl<>(deptEmpResponseList,pageable,deptEmps.getTotalElements());
    }

//    @Transactional(readOnly = true)
//    public DeptEmpResponse deptEmpByDeptNoAndEmpNo(String deptNo, Integer empNo){
//        Optional<DeptEmp> deptEmp1 = deptEmpRepository.findById(deptNo)
//
//        DeptEmp deptEmp = deptEmpRepository.findByDeptNoAndEmpNo(deptNo,empNo)
//
//        if (deptEmp)
//
//    }


}