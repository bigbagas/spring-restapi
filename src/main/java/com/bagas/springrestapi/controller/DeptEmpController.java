package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.DeptEmpResponse;
import com.bagas.springrestapi.model.PagingResponse;
import com.bagas.springrestapi.model.RegisterDeptEmpRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.DeptEmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class DeptEmpController {

    @Autowired
    private DeptEmpService deptEmpService;

    @PostMapping(
            path = "/dept/employees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerDeptEmployee(@RequestBody RegisterDeptEmpRequest request){

        deptEmpService.registerDeptEmp(request);

        return WebResponse.<String>builder()
                .data("OK").build();


    }

    @GetMapping(
            path = "/dept/{deptNo}/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptEmpResponse>> deptEmpAllEmployeeByDeptNo(@PathVariable("deptNo") String deptNo,
                                                                     @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                     @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DeptEmpResponse> deptEmpResponses = deptEmpService.deptEmpAllEmployeeByDeptNo(deptNo,page,size);

        return WebResponse.<List<DeptEmpResponse>>builder()
                .data(deptEmpResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptEmpResponses.getNumber())
                        .totalPage(deptEmpResponses.getTotalPages())
                        .size((int) deptEmpResponses.getTotalElements()).build())
                .build();

    }

    @GetMapping(
            path = "/dept/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptEmpResponse>> getDeptEmpAllEmployee(
                                                                     @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                     @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DeptEmpResponse> deptEmpResponses = deptEmpService.deptEmpAllEmployee(page,size);

        return WebResponse.<List<DeptEmpResponse>>builder()
                .data(deptEmpResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptEmpResponses.getNumber())
                        .totalPage(deptEmpResponses.getTotalPages())
                        .size((int) deptEmpResponses.getTotalElements()).build())
                .build();

    }


}
