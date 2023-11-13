package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping(
            path = "/departments",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<String> registerDepartment(@RequestBody RegisterDepartmentRequest request){
        departmentService.registerDepartment(request);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @PutMapping(
            path = "/departments/{deptNo}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<DepartmentResponse> updateDepartment(@PathVariable String deptNo, @RequestBody UpdateDepartmentRequest request){
        DepartmentResponse departmentResponse = departmentService.updateDepartment(deptNo, request);
        return WebResponse.<DepartmentResponse>builder()
                .data(departmentResponse)
                .build();
    }

    @GetMapping(
            path = "/departments/{deptNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<DepartmentResponse> getDepartmentByDeptNo(@PathVariable String deptNo){
        DepartmentResponse departmentResponse = departmentService.getDepartmentByDeptNo(deptNo);
        return WebResponse.<DepartmentResponse>builder()
                .data(departmentResponse)
                .build();
    }

    @DeleteMapping(
            path = "/departments/{deptNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    private WebResponse<String> deleteDepartment(@PathVariable String deptNo){
        departmentService.deleteDepartment(deptNo);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @GetMapping(
            path = "/departments/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DepartmentResponse>> searchDepartment(@RequestParam(value = "keyword",required = false)String keyword,
                                                                  @RequestParam(value = "size",required = false,defaultValue = "10")Integer size,
                                                                  @RequestParam(value = "page",required = false,defaultValue = "0")Integer page){
        Page<DepartmentResponse> departmentResponses = departmentService.searchDepartment(keyword,page,size);
        return WebResponse.<List<DepartmentResponse>>builder()
                .data(departmentResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(departmentResponses.getNumber())
                        .totalPage(departmentResponses.getTotalPages())
                        .size((int) departmentResponses.getTotalElements())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/departments",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DepartmentResponse>> getAllDepartment(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                           @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DepartmentResponse> departmentResponses = departmentService.getAllDepartment(page,size);
        return WebResponse.<List<DepartmentResponse>>builder()
                .data(departmentResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(departmentResponses.getNumber())
                        .totalPage(departmentResponses.getTotalPages())
                        .size((int) departmentResponses.getTotalElements())
                        .build())
                .build();
    }

}
