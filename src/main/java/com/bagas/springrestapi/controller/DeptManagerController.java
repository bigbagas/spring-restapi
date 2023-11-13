package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.DeptManagerResponse;
import com.bagas.springrestapi.model.PagingResponse;
import com.bagas.springrestapi.model.RegisterDeptManagerRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.DeptManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class DeptManagerController {

    @Autowired
    private DeptManagerService deptManagerService;

    @PostMapping(
            path = "departments/managers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerDeptManager(@RequestBody RegisterDeptManagerRequest request){

        System.out.println(request);
        deptManagerService.registerDeptManager(request);

        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/departments/managers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptManagerResponse>> getAllDeptManagers(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                     @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DeptManagerResponse> deptManagerResponses = deptManagerService.getAllDeptManagers(page,size);

        return WebResponse.<List<DeptManagerResponse>>builder()
                .data(deptManagerResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptManagerResponses.getNumber())
                        .totalPage(deptManagerResponses.getTotalPages())
                        .size((int) deptManagerResponses.getTotalElements()).build())
                .build();
    }

    @GetMapping(
            path = "/departments/{deptNo}/managers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptManagerResponse>> getDeptManagersByDeptNo(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                          @RequestParam(value = "size",required = false,defaultValue = "10")Integer size,
                                                                          @PathVariable("deptNo")String deptNo){
        Page<DeptManagerResponse> deptManagerResponses = deptManagerService.getDeptManagerByDeptNo(deptNo,page,size);

        return WebResponse.<List<DeptManagerResponse>>builder()
                .data(deptManagerResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptManagerResponses.getNumber())
                        .totalPage(deptManagerResponses.getTotalPages())
                        .size((int) deptManagerResponses.getTotalElements()).build())
                .build();
    }



}
