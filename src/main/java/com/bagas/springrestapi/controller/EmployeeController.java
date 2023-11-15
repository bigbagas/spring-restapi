package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(
            path = "/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerEmployee(@RequestBody RegisterEmployeeRequest request) throws ParseException {
        employeeService.registerEmployee(request);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(EmployeeController.class).registerEmployee(request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @PutMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> updateEmployee(@PathVariable Integer empNo,
                                                        @RequestBody UpdateEmployeeRequest request) throws ParseException {
        EmployeeResponse employeeResponse = employeeService.updateEmployee(empNo,request);
        WebResponse<EmployeeResponse> webResponse = WebResponse.<EmployeeResponse>builder()
                .data(employeeResponse)
                .build();
        Link selfLink = linkTo(methodOn(EmployeeController.class).updateEmployee(empNo,request)).withSelfRel();
        webResponse.add(selfLink);

        return webResponse;
    }

    @GetMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<EmployeeResponse> getEmployeeByEmpNo(@PathVariable Integer empNo){
        EmployeeResponse employeeResponse = employeeService.getEmployeeByEmpNo(empNo);
        WebResponse<EmployeeResponse> webResponse = WebResponse.<EmployeeResponse>builder()
                .data(employeeResponse)
                .build();
        Link selfLink = linkTo(methodOn(EmployeeController.class).getEmployeeByEmpNo(empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @DeleteMapping(
            path = "/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteEmployee(@PathVariable Integer empNo){
        employeeService.deleteEmployee(empNo);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(EmployeeController.class).deleteEmployee(empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> getAllEmployee(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                              @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<EmployeeResponse> employeeResponses = employeeService.allEmployee(page,size);

        WebResponse<List<EmployeeResponse>> webResponse = WebResponse.<List<EmployeeResponse>>builder()
                .data(employeeResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(employeeResponses.getNumber())
                        .totalPage(employeeResponses.getTotalPages())
                        .size((int) employeeResponses.getTotalElements())
                        .build())
                .build();

        Link selfLink = linkTo(methodOn(EmployeeController.class).getAllEmployee(page,size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/employees/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<EmployeeResponse>> searchEmployee(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                               @RequestParam(value = "size",required = false,defaultValue = "10")Integer size,
                                                              @RequestParam(value = "keyword",required = false)String keyword){
        Page<EmployeeResponse> employeeResponses = employeeService.searchEmployee(keyword,page,size);

        WebResponse<List<EmployeeResponse>> webResponse = WebResponse.<List<EmployeeResponse>>builder()
                .data(employeeResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(employeeResponses.getNumber())
                        .totalPage(employeeResponses.getTotalPages())
                        .size((int) employeeResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(EmployeeController.class).searchEmployee(page,size,keyword)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }


}
