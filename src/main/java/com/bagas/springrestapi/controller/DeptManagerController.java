package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.RegisterDeptManagerRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.DeptManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
