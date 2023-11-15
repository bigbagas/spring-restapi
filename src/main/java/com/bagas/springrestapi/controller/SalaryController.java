package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public WebResponse<String> registerSalary(@RequestBody RegisterSalaryRequest request) throws ParseException {
        salaryService.registerSalary(request);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(SalaryController.class).registerSalary(request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/salaries/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SalaryResponse> getSalaryByEmpNo(@PathVariable("empNo")Integer empNo){
        SalaryResponse salaryResponse = salaryService.getSalaryByEmpNo(empNo);
        WebResponse<SalaryResponse> webResponse =WebResponse.<SalaryResponse>builder()
                .data(salaryResponse)
                .build();
        Link selfLink = linkTo(methodOn(SalaryController.class).getSalaryByEmpNo(empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;

    }

    @GetMapping(
            path = "/salaries",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SalaryResponse>> getAllSalary(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<SalaryResponse> salaryResponses = salaryService.getAllSalary(page,size);
        WebResponse<List<SalaryResponse>> webResponse = WebResponse.<List<SalaryResponse>>builder()
                .data(salaryResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(salaryResponses.getNumber())
                        .totalPage(salaryResponses.getTotalPages())
                        .size((int) salaryResponses.getTotalElements()).build())
                .build();
        Link selfLink = linkTo(methodOn(SalaryController.class).getAllSalary(page, size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @PutMapping(
            path = "/salaries/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<SalaryResponse> updateSalary(@PathVariable("empNo")Integer empNo,
                                                    @RequestBody UpdateSalaryRequest request) throws ParseException {
        SalaryResponse salaryResponse = salaryService.updateSalary(empNo,request);
        WebResponse<SalaryResponse> webResponse = WebResponse.<SalaryResponse>builder()
                .data(salaryResponse)
                .build();
        Link selfLink = linkTo(methodOn(SalaryController.class).updateSalary(empNo, request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @DeleteMapping(
            path = "/salaries/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteSalary(@PathVariable("empNo")Integer empNo){
        salaryService.deleteSalary(empNo);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(SalaryController.class).deleteSalary(empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/salaries/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<SalaryResponse>> searchSalary(@RequestParam(value = "keyword",required = false)Integer keyword,
                                                          @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                          @RequestParam(value = "size",required = false,defaultValue = "10") Integer size){
        Page<SalaryResponse> salaryResponses = salaryService.searchSalary(keyword,page,size);
        WebResponse<List<SalaryResponse>> webResponse = WebResponse.<List<SalaryResponse>>builder()
                .data(salaryResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(salaryResponses.getNumber())
                        .totalPage(salaryResponses.getTotalPages())
                        .size((int) salaryResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(SalaryController.class).searchSalary(keyword,page,size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;


    }
}
