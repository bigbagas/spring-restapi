package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptManager;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.DeptEmpResponse;
import com.bagas.springrestapi.model.DeptManagerResponse;
import com.bagas.springrestapi.model.RegisterDeptManagerRequest;
import com.bagas.springrestapi.model.UpdateDeptManagerRequest;
import com.bagas.springrestapi.repository.DepartmentRepository;
import com.bagas.springrestapi.repository.DeptManagerRepository;
import com.bagas.springrestapi.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
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

    public void registerDeptManager(RegisterDeptManagerRequest request) throws ParseException {
        validationService.validate(request);
        validationService.dateValidation(request.getFromDate(),request.getToDate());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Department department = departmentRepository.findById(request.getDeptNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(request.getEmpNo())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Optional<DeptManager> deptManagerCheckCurrentDept = deptManagerRepository.findByDepartment_DeptNoAndAndEmpNo(request.getDeptNo(), request.getEmpNo());
        if (deptManagerCheckCurrentDept.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Manager is already registered in this Department");
        }

        Optional<DeptManager> deptManagerCheckOtherDept = deptManagerRepository.findById(request.getEmpNo());
        if (deptManagerCheckOtherDept.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Manager is already registered in other Department");
        }

        DeptManager deptManager = new DeptManager();
        deptManager.setFromDate(sdf.parse(request.getFromDate()));
        deptManager.setToDate(sdf.parse(request.getToDate()));
        deptManager.setDepartment(department);
        deptManager.setEmployee(employee);
        deptManagerRepository.save(deptManager);
    }

    @Transactional(readOnly = true)
    public Page<DeptManagerResponse> getAllDeptManager(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("empNo").ascending());
        Page<DeptManager> deptManagers = deptManagerRepository.findAll(pageable);
        List<DeptManagerResponse> deptEmpResponseList = deptManagers.stream()
                .map(this::toDeptManagerResponse).toList();
        return new PageImpl<>(deptEmpResponseList,pageable,deptManagers.getTotalElements());
    }

    private DeptManagerResponse toDeptManagerResponse(DeptManager deptManager){
        return DeptManagerResponse.builder()
                .empNo(deptManager.getEmpNo())
                .deptNo(deptManager.getDepartment().getDeptNo())
                .fromDate(deptManager.getFromDate())
                .toDate(deptManager.getToDate())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<DeptManagerResponse> getDeptManagerByDeptNo(String deptNo,Integer page, Integer size){
        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Pageable pageable = PageRequest.of(page,size,Sort.by("empNo").ascending());
        Page<DeptManager> deptManagers = deptManagerRepository.findByDepartment_DeptNo(deptNo,pageable);
        List<DeptManagerResponse> deptManagerResponseList = deptManagers.stream()
                .map(this::toDeptManagerResponse).toList();
        return new PageImpl<>(deptManagerResponseList,pageable,deptManagers.getTotalElements());
    }

    @Transactional(readOnly = true)
    public DeptManagerResponse getDeptManagerByDeptNoAndEmpNo(String deptNo, Integer empNo){
        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptManager deptManager = deptManagerRepository.findByDepartment_DeptNoAndAndEmpNo(deptNo,empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department or Employee is not found"));
        return toDeptManagerResponse(deptManager);
    }

    @Transactional
    public void deleteDeptManager(String deptNo, Integer empNo){
        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptManager deptManager = deptManagerRepository.findByDepartment_DeptNoAndAndEmpNo(deptNo,empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department or Employee is not found"));
        deptManagerRepository.delete(deptManager);
    }

//    @Transactional
//    public void deleteDeptManagerByDeptNo(String deptNo){
//        Department department = departmentRepository.findById(deptNo)
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));
//
//        List<DeptManager> deptManagers = deptManagerRepository.findAllByDepartment_DeptNo(deptNo);
//        if (deptManagers.isEmpty()){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found");
//        }
//        deptManagerRepository.deleteAll(deptManagers);
//    }

    @Transactional
    public DeptManagerResponse updateDeptManager(String deptNo, Integer empNo, UpdateDeptManagerRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        DeptManager deptManager = deptManagerRepository.findByDepartment_DeptNoAndAndEmpNo(deptNo,empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department or Employee is not found"));

        String newFromDate = sdf.format(deptManager.getFromDate());
        String newToDate = sdf.format(deptManager.getToDate());

        if (Objects.nonNull(request.getFromDate())){
            newFromDate = request.getFromDate();
            deptManager.setFromDate(sdf.parse(request.getFromDate()));
        }

        if (Objects.nonNull(request.getToDate())){
            newToDate = request.getToDate();
            deptManager.setToDate(sdf.parse(request.getToDate()));
        }

        validationService.dateValidation(newFromDate,newToDate);

        if (Objects.nonNull(request.getDeptNo())){
            Department newDept = departmentRepository.findById(request.getDeptNo())
                    .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"New Department is not found"));
            deptManager.setDepartment(newDept);
        }

        deptManagerRepository.save(deptManager);
        return toDeptManagerResponse(deptManager);
    }

}
