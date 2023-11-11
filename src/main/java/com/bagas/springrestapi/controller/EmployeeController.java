package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.EmployeesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping(
            path = "/employees/search/all",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> findAllEmployee(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                               @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<EmployeeResponse> employeeResponses = employeesService.allEmployee(page,size);
        return WebResponse.<List<EmployeeResponse>>builder()
                .data(employeeResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(employeeResponses.getNumber())
                        .totalPage(employeeResponses.getTotalPages())
                        .size((int) employeeResponses.getTotalElements()).build())
                .build();
    }
}
