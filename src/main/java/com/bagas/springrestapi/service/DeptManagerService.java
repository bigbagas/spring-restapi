package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptManager;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.RegisterDeptManagerRequest;
import com.bagas.springrestapi.repository.DepartmentRepository;
import com.bagas.springrestapi.repository.DeptManagerRepository;
import com.bagas.springrestapi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class DeptManagerService {

    @Autowired
    private DeptManagerRepository deptManagerRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void registerDeptManager(RegisterDeptManagerRequest request){
        validationService.validate(request);
        System.out.println(request);

        Department department = departmentRepository.findById(request.getDeptNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(request.getEmpNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));


        Optional<DeptManager> deptManagerCheck = deptManagerRepository.findByDepartment_DeptNoAndAndEmpNo(request.getDeptNo(), request.getEmpNo());

        if (deptManagerCheck.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Manager is already registered in this Department");
        }

        Optional<DeptManager> deptManagerCheck2 = deptManagerRepository.findById(request.getEmpNo());

        if (deptManagerCheck2.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Manager is already registered in other Department");
        }



        DeptManager deptManager = new DeptManager();
        deptManager.setFromDate(request.getFromDate());
        deptManager.setToDate(request.getToDate());
        deptManager.setDepartment(department);
        deptManager.setEmployee(employee);
        deptManagerRepository.save(deptManager);

    }

}
