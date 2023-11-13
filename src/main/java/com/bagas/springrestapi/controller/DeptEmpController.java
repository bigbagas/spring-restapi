package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
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
            path = "/departments/employees",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerDeptEmployee(@RequestBody RegisterDeptEmpRequest request){

        deptEmpService.registerDeptEmp(request);

        return WebResponse.<String>builder()
                .data("OK").build();


    }

    @GetMapping(
            path = "/departments/{deptNo}/employees",
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
//
    @GetMapping(
            path = "/departments/employees",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptEmpResponse>> getDeptEmpAllEmployee(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
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

    @GetMapping(
            path = "/departments/{deptNo}/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeptEmpResponse> deptEmpByDeptNoAndEmpNo(@PathVariable("deptNo") String deptNo,
                                                                    @PathVariable("empNo")Integer empNo){
        DeptEmpResponse deptEmpResponse = deptEmpService.deptEmpByDeptNoAndEmpNo(deptNo,empNo);

        return WebResponse.<DeptEmpResponse>builder().data(deptEmpResponse).build();

    }

    @PutMapping(
            path = "/departments/{deptNo}/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> updateDeptEmp(@PathVariable("deptNo")String deptNo,
                                             @PathVariable("empNo")Integer empNo,
                                             @RequestBody UpdateDeptEmpRequest request){
        deptEmpService.updateDeptEmp(deptNo,empNo,request);

        return WebResponse.<String>builder()
                .data("OK").build();

    }

    @DeleteMapping(
            path = "/departments/{deptNo}/employees/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteDeptEmp(@PathVariable("deptNo")String deptNo,
                                             @PathVariable("empNo")Integer empNo){
        deptEmpService.deleteDeptEmp(deptNo,empNo);

        return WebResponse.<String>builder()
                .data("OK").build();

    }


}
