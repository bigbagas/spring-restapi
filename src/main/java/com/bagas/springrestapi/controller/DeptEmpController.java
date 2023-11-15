package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.DeptEmpService;
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
public class DeptEmpController {

    @Autowired
    private DeptEmpService deptEmpService;

    @PostMapping(
            path = "/departments/employees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerDeptEmp(@RequestBody RegisterDeptEmpRequest request) throws ParseException {
        deptEmpService.registerDeptEmp(request);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();

        Link selfLink = linkTo(methodOn(DeptEmpController.class).registerDeptEmp(request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/departments/{deptNo}/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptEmpResponse>> getDeptEmpByDeptNo(@PathVariable("deptNo") String deptNo,
                                                                 @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                 @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DeptEmpResponse> deptEmpResponses = deptEmpService.getDeptEmpByDeptNo(deptNo,page,size);
        WebResponse<List<DeptEmpResponse>> webResponse = WebResponse.<List<DeptEmpResponse>>builder()
                .data(deptEmpResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptEmpResponses.getNumber())
                        .totalPage(deptEmpResponses.getTotalPages())
                        .size((int) deptEmpResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(DeptEmpController.class).getDeptEmpByDeptNo(deptNo,page,size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/departments/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptEmpResponse>> getAllDeptEmp(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                            @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DeptEmpResponse> deptEmpResponses = deptEmpService.getAllDeptEmp(page,size);

        WebResponse<List<DeptEmpResponse>> webResponse = WebResponse.<List<DeptEmpResponse>>builder()
                .data(deptEmpResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptEmpResponses.getNumber())
                        .totalPage(deptEmpResponses.getTotalPages())
                        .size((int) deptEmpResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(DeptEmpController.class).getAllDeptEmp(page,size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/departments/{deptNo}/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeptEmpResponse> getDeptEmpByDeptNoAndEmpNo(@PathVariable("deptNo") String deptNo,
                                                                   @PathVariable("empNo")Integer empNo){
        DeptEmpResponse deptEmpResponse = deptEmpService.deptEmpByDeptNoAndEmpNo(deptNo,empNo);
        WebResponse<DeptEmpResponse> webResponse = WebResponse.<DeptEmpResponse>builder()
                .data(deptEmpResponse)
                .build();
        Link selfLink = linkTo(methodOn(DeptEmpController.class).getDeptEmpByDeptNoAndEmpNo(deptNo,empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @PutMapping(
            path = "/departments/{deptNo}/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeptEmpResponse> updateDeptEmp(@PathVariable("deptNo")String deptNo,
                                             @PathVariable("empNo")Integer empNo,
                                             @RequestBody UpdateDeptEmpRequest request) throws ParseException {
        DeptEmpResponse deptEmpResponse = deptEmpService.updateDeptEmp(deptNo,empNo,request);
        WebResponse<DeptEmpResponse> webResponse = WebResponse.<DeptEmpResponse>builder()
                .data(deptEmpResponse)
                .build();
        Link selfLink = linkTo(methodOn(DeptEmpController.class).updateDeptEmp(deptNo,empNo,request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @DeleteMapping(
            path = "/departments/{deptNo}/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteDeptEmp(@PathVariable("deptNo")String deptNo,
                                             @PathVariable("empNo")Integer empNo){
        deptEmpService.deleteDeptEmp(deptNo,empNo);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(DeptEmpController.class).deleteDeptEmp(deptNo,empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

}
