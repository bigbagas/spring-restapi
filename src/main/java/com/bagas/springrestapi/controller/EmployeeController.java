package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.EmployeeService;
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
    private EmployeeService employeeService;

    @PostMapping(
            path = "/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request){
        System.out.println("gender = "+request.getGender());
        employeeService.registerEmployee(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PutMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> updateEmployee(@PathVariable Integer empNo,
                                                        @RequestBody UpdateEmployeeRequest request){
        EmployeeResponse employeeResponse = employeeService.updateEmployee(empNo,request);

        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();
    }

    @GetMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> getEmployeeByEmpNo(@PathVariable Integer empNo){

        EmployeeResponse employeeResponse = employeeService.getEmployeeByEmpNo(empNo);

        return WebResponse.<EmployeeResponse>builder().data(employeeResponse).build();

    }

    @DeleteMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteEmployee(@PathVariable Integer empNo){
        employeeService.deleteEmployee(empNo);

        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> findAllEmployee(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                               @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<EmployeeResponse> employeeResponses = employeeService.allEmployee(page,size);
        return WebResponse.<List<EmployeeResponse>>builder()
                .data(employeeResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(employeeResponses.getNumber())
                        .totalPage(employeeResponses.getTotalPages())
                        .size((int) employeeResponses.getTotalElements()).build())
                .build();
    }

    @GetMapping(
            path = "/employees/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> searchEmployee(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                               @RequestParam(value = "size",required = false,defaultValue = "10")Integer size,
                                                              @RequestParam(value = "keyword",required = false)String keyword){
        Page<EmployeeResponse> employeeResponses = employeeService.searchEmployee(keyword,page,size);
        return WebResponse.<List<EmployeeResponse>>builder()
                .data(employeeResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(employeeResponses.getNumber())
                        .totalPage(employeeResponses.getTotalPages())
                        .size((int) employeeResponses.getTotalElements()).build())
                .build();
    }

}
