package com.bagas.springrestapi.service;

import com.bagas.springrestapi.controller.DepartmentController;
import com.bagas.springrestapi.controller.DeptEmpController;
import com.bagas.springrestapi.controller.EmployeeController;
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
import org.springframework.hateoas.Link;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
        validationService.dateValidation(request.getFromDate(),request.getToDate());
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
        List<DeptEmp> deptEmpList = deptEmps.getContent();
        for (int i = 0; i < deptEmpList.size(); i++) {
            DeptEmp deptEmp = deptEmpList.get(i);
            addLink(deptEmp);
        }
        List<DeptEmpResponse> deptEmpResponseList = deptEmps.stream()
                .map(this::toDeptEmpResponse).toList();
        return new PageImpl<>(deptEmpResponseList,pageable,deptEmps.getTotalElements());
    }

    private DeptEmpResponse toDeptEmpResponse(DeptEmp deptEmp){
        DeptEmpResponse deptEmpResponse = DeptEmpResponse.builder()
                .deptNo(deptEmp.getDepartment().getDeptNo())
                .empNo(deptEmp.getEmpNo())
                .fromDate(deptEmp.getFromDate())
                .toDate(deptEmp.getToDate())
                .build();
        deptEmpResponse.add(deptEmp.getLinks());
        return deptEmpResponse;
    }

    @Transactional(readOnly = true)
    public Page<DeptEmpResponse> getAllDeptEmp(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("empNo").ascending());
        Page<DeptEmp> deptEmps = deptEmpRepository.findAll(pageable);
        List<DeptEmp> deptEmpList = deptEmps.getContent();
        for (int i = 0; i < deptEmpList.size(); i++) {
            DeptEmp deptEmp = deptEmpList.get(i);
            addLink(deptEmp);
        }
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
        addLink(deptEmp);
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

        String newFromDate = sdf.format(deptEmp.getFromDate());
        String newToDate = sdf.format(deptEmp.getToDate());

        if (Objects.nonNull(request.getFromDate())){
            newFromDate = request.getFromDate();
            deptEmp.setFromDate(sdf.parse(request.getFromDate()));
        }

        if (Objects.nonNull(request.getToDate())){
            newToDate = request.getToDate();
            deptEmp.setToDate(sdf.parse(request.getToDate()));
        }

        validationService.dateValidation(newFromDate,newToDate);

        if (Objects.nonNull(request.getDeptNo())){
            Department newDept = departmentRepository.findById(request.getDeptNo())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"New Department is not found"));
            deptEmp.setDepartment(newDept);
        }

        deptEmpRepository.save(deptEmp);
        addLink(deptEmp);
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

    private void addLink(DeptEmp deptEmp){
        Link link = linkTo(methodOn(DeptEmpController.class).getDeptEmpByDeptNoAndEmpNo(deptEmp.getDepartment().getDeptNo(),deptEmp.getEmpNo())).withSelfRel();
        deptEmp.add(link);

        if (employeeRepository.findById(deptEmp.getEmpNo()).isPresent()){
            Link linkEmployee = linkTo(methodOn(EmployeeController.class).getEmployeeByEmpNo(deptEmp.getEmpNo())).withRel("employee");
            deptEmp.add(linkEmployee);
        }

        if (departmentRepository.findById(deptEmp.getDepartment().getDeptNo()).isPresent()){
            Link linkDepartment = linkTo(DepartmentController.class).slash(deptEmp.getDepartment().getDeptNo()).withRel("department");
            deptEmp.add(linkDepartment);
        }
    }

}
