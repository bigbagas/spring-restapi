package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.RegisterTitleRequest;
import com.bagas.springrestapi.model.TitleResponse;
import com.bagas.springrestapi.model.UpdateTitleRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping(path = "/api")
public class TitleController {

    @Autowired
    private TitleService titleService;

    @PostMapping(
            path = "/titles",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> regiterTitle(@RequestBody RegisterTitleRequest request) throws ParseException {
        titleService.registerTitle(request);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }

    @PutMapping(
            path = "/titles/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TitleResponse> updateTitle(@PathVariable("empNo")Integer empNo,
                                                  @RequestBody UpdateTitleRequest request) throws ParseException {
        TitleResponse titleResponse = titleService.updateTitle(empNo,request);
        return WebResponse.<TitleResponse>builder()
                .data(titleResponse)
                .build();
    }

    @GetMapping(
            path = "/titles/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TitleResponse> getTitleByEmpNo(@PathVariable("empNo")Integer empNo){

        TitleResponse titleResponse = titleService.getTitleByEmpNo(empNo);
        return WebResponse.<TitleResponse>builder()
                .data(titleResponse)
                .build();
    }

    @DeleteMapping(
            path = "/titles/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteTitle(@PathVariable("empNo") Integer empNo){
        titleService.deleteTitle(empNo);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }
}
