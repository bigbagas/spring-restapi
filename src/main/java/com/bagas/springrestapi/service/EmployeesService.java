package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.enums.Gender;
import com.bagas.springrestapi.model.EmployeeResponse;
import com.bagas.springrestapi.model.RegisterEmployeeRequest;
import com.bagas.springrestapi.model.UpdateEmployeeRequest;
import com.bagas.springrestapi.repository.EmployeesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class EmployeesService {

    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void registerEmployee(RegisterEmployeeRequest request){

        validationService.validate(request);

        Employee employee = new Employee();
        employee.setBirthDate(request.getBirthDate());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        System.out.println(request.getGender());
        if (request.getGender().equalsIgnoreCase("M")){

            employee.setGender(Gender.M.name());
        }else if (request.getGender().equalsIgnoreCase("F")){
            employee.setGender(Gender.F.name());
        }



        employee.setHireDate(request.getHireDate());
        employeesRepository.save(employee);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Integer empNo,UpdateEmployeeRequest request){
        validationService.validate(request);

        Employee employee = employeesRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        if (Objects.nonNull(request.getBirthDate())){
            employee.setBirthDate(request.getBirthDate());
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
            employee.setHireDate(request.getHireDate());
        }

        employeesRepository.save(employee);

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

        Employee employeeByEmpNo = employeesRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        return toEmployeeResponse(employeeByEmpNo);

    }

    @Transactional
    public void deleteEmployee(Integer empNo){
        Employee employee = employeesRepository.findById(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        employeesRepository.delete(employee);
    }
}
