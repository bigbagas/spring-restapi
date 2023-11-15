package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.TitleService;
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
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(TitleController.class).regiterTitle(request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @PutMapping(
            path = "/titles/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TitleResponse> updateTitle(@PathVariable("empNo")Integer empNo,
                                                  @RequestBody UpdateTitleRequest request) throws ParseException {
        TitleResponse titleResponse = titleService.updateTitle(empNo,request);
        WebResponse<TitleResponse> webResponse = WebResponse.<TitleResponse>builder()
                .data(titleResponse)
                .build();
        Link selfLink = linkTo(methodOn(TitleController.class).updateTitle(empNo,request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/titles/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TitleResponse> getTitleByEmpNo(@PathVariable("empNo")Integer empNo){

        TitleResponse titleResponse = titleService.getTitleByEmpNo(empNo);
        WebResponse<TitleResponse> webResponse = WebResponse.<TitleResponse>builder()
                .data(titleResponse)
                .build();
        Link selfLink = linkTo(methodOn(TitleController.class).getTitleByEmpNo(empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @DeleteMapping(
            path = "/titles/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteTitle(@PathVariable("empNo") Integer empNo){
        titleService.deleteTitle(empNo);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(TitleController.class).deleteTitle(empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/titles",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TitleResponse>> getAllTitle(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                        @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){

        Page<TitleResponse> titleResponses = titleService.getAllTitle(page, size);
        WebResponse<List<TitleResponse>> webResponse = WebResponse.<List<TitleResponse>>builder()
                .data(titleResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(titleResponses.getNumber())
                        .totalPage(titleResponses.getTotalPages())
                        .size((int) titleResponses.getTotalElements()).build())
                .build();
        Link selfLink = linkTo(methodOn(TitleController.class).getAllTitle(page, size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/titles/search",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TitleResponse>> searchTitle(@RequestParam(value = "keyword",required = false)String keyword,
                                                        @RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                        @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){

        Page<TitleResponse> titleResponses = titleService.searchTitle(keyword,page, size);
        WebResponse<List<TitleResponse>> webResponse = WebResponse.<List<TitleResponse>>builder()
                .data(titleResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(titleResponses.getNumber())
                        .totalPage(titleResponses.getTotalPages())
                        .size((int) titleResponses.getTotalElements()).build())
                .build();
        Link selfLink = linkTo(methodOn(TitleController.class).searchTitle(keyword,page, size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;

    }




}
