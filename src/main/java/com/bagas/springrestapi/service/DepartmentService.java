package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.model.DepartmentResponse;
import com.bagas.springrestapi.model.RegisterDepartmentRequest;
import com.bagas.springrestapi.model.UpdateDepartmentRequest;
import com.bagas.springrestapi.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ValidationService validationService;

    public void registerDepartment(RegisterDepartmentRequest request){
        validationService.validate(request);

        Department department = new Department();
        department.setDeptNo(request.getDeptNo());
        department.setDeptName(request.getDeptName());
        departmentRepository.save(department);
    }

    public DepartmentResponse updateDepartment(String deptNo,UpdateDepartmentRequest request){
        validationService.validate(request);

        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        if (Objects.nonNull(request.getDeptName())){
            department.setDeptName(request.getDeptName());
            departmentRepository.save(department);
        }

        return toDepartmentResponse(department);


    }

    private DepartmentResponse toDepartmentResponse(Department department){
        return DepartmentResponse.builder()
                .deptNo(department.getDeptNo())
                .deptName(department.getDeptName()).build();
    }
}
