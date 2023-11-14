package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

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

    @GetMapping(
            path = "/titles",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TitleResponse>> getAllTitle(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                        @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){

        Page<TitleResponse> titleResponses = titleService.getAllTitle(page, size);
        return WebResponse.<List<TitleResponse>>builder()
                .data(titleResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(titleResponses.getNumber())
                        .totalPage(titleResponses.getTotalPages())
                        .size((int) titleResponses.getTotalElements()).build())
                .build();

    }

    @GetMapping(
            path = "/titles/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TitleResponse>> searchTitle(@RequestParam(value = "keyword",required = false)String keyword,
                                                        @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                        @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){

        Page<TitleResponse> titleResponses = titleService.searchTitle(keyword,page, size);
        return WebResponse.<List<TitleResponse>>builder()
                .data(titleResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(titleResponses.getNumber())
                        .totalPage(titleResponses.getTotalPages())
                        .size((int) titleResponses.getTotalElements()).build())
                .build();

    }




}
