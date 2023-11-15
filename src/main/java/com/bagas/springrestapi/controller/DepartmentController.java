package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerDepartment(@RequestBody RegisterDepartmentRequest request){
        departmentService.registerDepartment(request);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(DepartmentController.class).registerDepartment(request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @PutMapping(
            path = "/{deptNo}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DepartmentResponse> updateDepartment(@PathVariable String deptNo, @RequestBody UpdateDepartmentRequest request){
        DepartmentResponse departmentResponse = departmentService.updateDepartment(deptNo, request);
        WebResponse<DepartmentResponse> webResponse = WebResponse.<DepartmentResponse>builder()
                .data(departmentResponse)
                .build();
        Link selfLink = linkTo(methodOn(DepartmentController.class).updateDepartment(deptNo,request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/{deptNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DepartmentResponse> getDepartmentByDeptNo(@PathVariable String deptNo){
        DepartmentResponse departmentResponse = departmentService.getDepartmentByDeptNo(deptNo);
        WebResponse<DepartmentResponse> webResponse = WebResponse.<DepartmentResponse>builder()
                .data(departmentResponse)
                .build();
        Link selfLink = linkTo(methodOn(DepartmentController.class).getDepartmentByDeptNo(deptNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @DeleteMapping(
            path = "/{deptNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteDepartment(@PathVariable String deptNo){
        departmentService.deleteDepartment(deptNo);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(DepartmentController.class).deleteDepartment(deptNo)).withSelfRel();
        webResponse.add(selfLink);

        return webResponse;
    }

    @GetMapping(
            path = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DepartmentResponse>> searchDepartment(@RequestParam(value = "keyword",required = false)String keyword,
                                                                  @RequestParam(value = "size",required = false,defaultValue = "10")Integer size,
                                                                  @RequestParam(value = "page",required = false,defaultValue = "0")Integer page){
        Page<DepartmentResponse> departmentResponses = departmentService.searchDepartment(keyword,page,size);

        WebResponse<List<DepartmentResponse>> webResponse = WebResponse.<List<DepartmentResponse>>builder()
                .data(departmentResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(departmentResponses.getNumber())
                        .totalPage(departmentResponses.getTotalPages())
                        .size((int) departmentResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(DepartmentController.class).searchDepartment(keyword,size,page)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(

            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DepartmentResponse>> getAllDepartment(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                           @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DepartmentResponse> departmentResponses = departmentService.getAllDepartment(page,size);

        WebResponse<List<DepartmentResponse>> webResponse = WebResponse.<List<DepartmentResponse>>builder()
                .data(departmentResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(departmentResponses.getNumber())
                        .totalPage(departmentResponses.getTotalPages())
                        .size((int) departmentResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(DepartmentController.class).getAllDepartment(size,page)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

}
