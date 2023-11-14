package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.model.RegisterSalaryRequest;
import com.bagas.springrestapi.model.SalaryResponse;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
}
