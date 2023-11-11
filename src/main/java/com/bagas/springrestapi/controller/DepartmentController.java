package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.DepartmentResponse;
import com.bagas.springrestapi.model.RegisterDepartmentRequest;
import com.bagas.springrestapi.model.UpdateDepartmentRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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

        return WebResponse.<String>builder().data("OK").build();
    }

    @PutMapping(
            path = "/departments/{deptNo}",
            consumes = MediaType.APPLICATION_JSON_VALUE

    )
    private WebResponse<DepartmentResponse> updateDepartment(@PathVariable String deptNo, @RequestBody UpdateDepartmentRequest request){
        DepartmentResponse departmentResponse = departmentService.updateDepartment(deptNo, request);

        return WebResponse.<DepartmentResponse>builder()
                .data(departmentResponse).build();
    }





}
