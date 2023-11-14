package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @PostMapping(
            path = "/salaries",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerSalary(@RequestBody RegisterSalaryRequest request){
        salaryService.registerSalary(request);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(
            path = "/salaries/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SalaryResponse> getSalaryByEmpNo(@PathVariable("empNo")Integer empNo){
        SalaryResponse salaryResponse = salaryService.getSalaryByEmpNo(empNo);
        return WebResponse.<SalaryResponse>builder()
                .data(salaryResponse)
                .build();
    }

    @GetMapping(
            path = "/salaries",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SalaryResponse>> getAllSalary(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<SalaryResponse> salaryResponses = salaryService.getAllSalary(page,size);


        return WebResponse.<List<SalaryResponse>>builder()
                .data(salaryResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(salaryResponses.getNumber())
                        .totalPage(salaryResponses.getTotalPages())
                        .size((int) salaryResponses.getTotalElements()).build())
                .build();
    }

    @PutMapping(
            path = "/salaries/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SalaryResponse> updateSalary(@PathVariable("empNo")Integer empNo,
                                                    @RequestBody UpdateSalaryRequest request){
        SalaryResponse salaryResponse = salaryService.updateSalary(empNo,request);
        return WebResponse.<SalaryResponse>builder()
                .data(salaryResponse)
                .build();
    }

    @DeleteMapping(
            path = "/salaries/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteSalary(@PathVariable("empNo")Integer empNo){
        salaryService.deleteSalary(empNo);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(
            path = "/salaries/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SalaryResponse>> searchSalary(@RequestParam(value = "keyword",required = false)Integer keyword,
                                                          @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10") Integer size){
        Page<SalaryResponse> salaryResponses = salaryService.searchSalary(keyword,page,size);
        return WebResponse.<List<SalaryResponse>>builder()
                .data(salaryResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(salaryResponses.getNumber())
                        .totalPage(salaryResponses.getTotalPages())
                        .size((int) salaryResponses.getTotalElements())
                        .build())
                .build();

    }
}
