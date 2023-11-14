package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptEmp;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.DeptEmpResponse;
import com.bagas.springrestapi.model.RegisterDeptEmpRequest;
import com.bagas.springrestapi.model.UpdateDeptEmpRequest;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public void registerDeptEmp (RegisterDeptEmpRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Department department = departmentRepository.findById(request.getDeptNo())
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(request.getEmpNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Optional<DeptEmp> deptEmpCheckCurrentDept = deptEmpRepository.findByDepartment_DeptNoAndAndEmpNo(request.getDeptNo(),request.getEmpNo());
        if (deptEmpCheckCurrentDept.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee is already register is this Department");
        }

        Optional<DeptEmp> deptEmpCheckOtherDept = deptEmpRepository.findById(request.getEmpNo());
        if (deptEmpCheckOtherDept.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee is already register in other Department");
        }

        DeptEmp deptEmp = new DeptEmp();
        deptEmp.setFromDate(sdf.parse(request.getFromDate()));
        deptEmp.setToDate(sdf.parse(request.getToDate()));
        deptEmp.setDepartment(department);
        deptEmp.setEmployee(employee);
        deptEmpRepository.save(deptEmp);
    }

    @Transactional(readOnly = true)
    public Page<DeptEmpResponse> getDeptEmpByDeptNo(String deptNo, Integer page, Integer size){
        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

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

    @Transactional(readOnly = true)
    public Page<DeptEmpResponse> getAllDeptEmp(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("empNo").ascending());
        Page<DeptEmp> deptEmps = deptEmpRepository.findAll(pageable);
        List<DeptEmpResponse> deptEmpResponseList = deptEmps.stream()
                .map(this::toDeptEmpResponse).toList();
        return new PageImpl<>(deptEmpResponseList,pageable,deptEmps.getTotalElements());
    }

    @Transactional(readOnly = true)
    public DeptEmpResponse deptEmpByDeptNoAndEmpNo(String deptNo, Integer empNo){
        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not Found"));

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptEmp deptEmp = deptEmpRepository.findByDepartment_DeptNoAndAndEmpNo(deptNo,empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department or Employee is not found"));
        return toDeptEmpResponse(deptEmp);
    }

    @Transactional
    public DeptEmpResponse updateDeptEmp (String deptNo, Integer empNo,UpdateDeptEmpRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not Found"));

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptEmp deptEmp = deptEmpRepository.findByDepartment_DeptNoAndAndEmpNo(deptNo,empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department or Employee is not found"));

        if (Objects.nonNull(request.getFromDate())){
            deptEmp.setFromDate(sdf.parse(request.getFromDate()));
        }

        if (Objects.nonNull(request.getToDate())){
            deptEmp.setToDate(sdf.parse(request.getToDate()));
        }

        if (Objects.nonNull(request.getDeptNo())){
            Department newDept = departmentRepository.findById(request.getDeptNo())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"New Department is not found"));
            deptEmp.setDepartment(newDept);
        }

        deptEmpRepository.save(deptEmp);
        return toDeptEmpResponse(deptEmp);
    }

    @Transactional
    public void deleteDeptEmp(String deptNo,Integer empNo){
        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not Found"));

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptEmp deptEmp = deptEmpRepository.findByDepartment_DeptNoAndAndEmpNo(deptNo,empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department or Employee is not found"));
        deptEmpRepository.delete(deptEmp);
    }

}
