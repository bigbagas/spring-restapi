package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.DeptEmp;
import com.bagas.springrestapi.entity.DeptManager;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.enums.Gender;
import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.repository.DeptEmpRepository;
import com.bagas.springrestapi.repository.DeptManagerRepository;
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
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private DeptEmpRepository deptEmpRepository;

    @Autowired
    private DeptManagerRepository deptManagerRepository;

    @Transactional
    public void registerEmployee(RegisterEmployeeRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Optional<Employee> employeeCheck = employeeRepository
                .findByBirthDateAndAndFirstNameAndLastNameAndHireDateAndGender(
                        sdf.parse(request.getBirthDate()),
                        request.getFirstName(),
                        request.getLastName(),
                        sdf.parse(request.getHireDate()),
                        request.getGender()
                );
        if (employeeCheck.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee already registered");
        }

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse(request.getBirthDate()));
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        if (request.getGender().equalsIgnoreCase("M")){
            employee.setGender(Gender.M.name());
        }else if (request.getGender().equalsIgnoreCase("F")){
            employee.setGender(Gender.F.name());
        }
        employee.setHireDate(sdf.parse(request.getHireDate()));
        employeeRepository.save(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Integer empNo,UpdateEmployeeRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        if (Objects.nonNull(request.getBirthDate())){
            employee.setBirthDate(sdf.parse((request.getBirthDate())));
        }

        if (Objects.nonNull(request.getFirstName())){
            employee.setFirstName(request.getFirstName());
        }

        if (Objects.nonNull(request.getLastName())){
            employee.setLastName(request.getLastName());
        }

        if (Objects.nonNull(request.getGender())){
            if (request.getGender().equalsIgnoreCase("M")){
                employee.setGender(Gender.M.name());
            }else if (request.getGender().equalsIgnoreCase("F")){
                employee.setGender(Gender.F.name());
            }
        }

        if (Objects.nonNull(request.getHireDate())){
            employee.setHireDate(sdf.parse(request.getHireDate()));
        }

        employeeRepository.save(employee);
        return toEmployeeResponse(employee);
    }

    private EmployeeResponse toEmployeeResponse(Employee employee){
        return EmployeeResponse.builder()
                .empNo(employee.getEmpNo())
                .birthDate(employee.getBirthDate())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .gender(employee.getGender())
                .hireDate(employee.getHireDate()
                ).build();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmpNo(Integer empNo){
        Employee employeeByEmpNo = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        System.out.println(employeeByEmpNo.getSalary());

        return toEmployeeResponse(employeeByEmpNo);
    }



    @Transactional
    public void deleteEmployee(Integer empNo){
        Employee employee = employeeRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Optional<DeptEmp> deptEmp = deptEmpRepository.findById(empNo);
        if (deptEmp.isPresent()){
            deptEmpRepository.delete(deptEmp.get());
        }

        Optional<DeptManager> deptManager = deptManagerRepository.findById(empNo);
        if (deptManager.isPresent()){
            deptManagerRepository.delete(deptManager.get());
        }

        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> allEmployee(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("empNo").ascending());
        Page<Employee> employees = employeeRepository.findAll(pageable);
        List<EmployeeResponse> employeeResponseList = employees.getContent().stream()
                .map(this::toEmployeeResponse).toList();
        return new PageImpl<>(employeeResponseList,pageable,employees.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployee(String keyword, Integer page, Integer size){
        Specification<Employee> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(keyword)){
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstName")), "%"+keyword.toLowerCase()+"%"
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")), "%"+keyword.toLowerCase()+"%"
                        )
                ));
            }

            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };

        Pageable pageable = PageRequest.of(page,size,Sort.by("empNo").ascending());
        Page<Employee> employees = employeeRepository.findAll(specification,pageable);
        List<EmployeeResponse> employeeResponseList = employees.getContent().stream()
                .map(this::toEmployeeResponse).toList();
        return new PageImpl<>(employeeResponseList,pageable,employees.getTotalElements());
    }

//    @Transactional(readOnly = true)
//    public EmployeeSalaryResponse getEmployeeSalaryByEmpNo(Integer empNo){
//        Employee employeeByEmpNo = employeeRepository.findById(empNo)
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));
//
//        System.out.println(employeeByEmpNo.getSalary());
//
//        return toEmployeeSalaryResponse(employeeByEmpNo);
//    }

//    private EmployeeSalaryResponse toEmployeeSalaryResponse(Employee employee){
//
//        Salary salary = employee.getSalary();
//
//        SalaryResponse salaryResponse = new SalaryResponse();
//        if (salary.isPresent()){
//            salaryResponse.setEmpNo(employee.getSalary().getEmpNo());
//            salaryResponse.setSalary(employee.getSalary().getSalary());
//            salaryResponse.setFromDate(employee.getSalary().getFromDate());
//            salaryResponse.setToDate(employee.getSalary().getToDate());
//
//        }
//
//        return EmployeeSalaryResponse.builder()
//                .empNo(employee.getEmpNo())
//                .birthDate(employee.getBirthDate())
//                .firstName(employee.getFirstName())
//                .lastName(employee.getLastName())
//                .gender(employee.getGender())
//                .hireDate(employee.getHireDate())
//                .salary(salary)
//                .build();
//    }

}
