package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.EmployeeResponse;
import com.bagas.springrestapi.model.RegisterEmployeeRequest;
import com.bagas.springrestapi.model.UpdateEmployeeRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.EmployeesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class EmployeeController {

    @Autowired
    private EmployeesService employeesService;

    @PostMapping(
            path = "/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request){
        System.out.println("gender = "+request.getGender());
        employeesService.registerEmployee(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PutMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> updateEmployee(@PathVariable Integer empNo,
                                                        @RequestBody UpdateEmployeeRequest request){
        EmployeeResponse employeeResponse = employeesService.updateEmployee(empNo,request);

        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @GetMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> getEmployeeByEmpNo(@PathVariable Integer empNo){

        EmployeeResponse employeeResponse = employeesService.getEmployeeByEmpNo(empNo);

        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();

    }

    @DeleteMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteEmployee(@PathVariable Integer empNo){
        employeesService.deleteEmployee(empNo);

        return WebResponse.<String>builder().data("OK").build();
    }
}
