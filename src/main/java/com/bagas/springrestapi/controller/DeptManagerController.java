package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.service.DeptManagerService;
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
public class DeptManagerController {

    @Autowired
    private DeptManagerService deptManagerService;

    @PostMapping(
            path = "departments/managers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> registerDeptManager(@RequestBody RegisterDeptManagerRequest request) throws ParseException {
        deptManagerService.registerDeptManager(request);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();

        Link selfLink = linkTo(methodOn(DeptManagerController.class).registerDeptManager(request)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/departments/managers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptManagerResponse>> getAllDeptManager(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                    @RequestParam(value = "size",required = false,defaultValue = "10")Integer size){
        Page<DeptManagerResponse> deptManagerResponses = deptManagerService.getAllDeptManager(page,size);
        WebResponse<List<DeptManagerResponse>> webResponse = WebResponse.<List<DeptManagerResponse>>builder()
                .data(deptManagerResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptManagerResponses.getNumber())
                        .totalPage(deptManagerResponses.getTotalPages())
                        .size((int) deptManagerResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(DeptManagerController.class).getAllDeptManager(page,size)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/departments/{deptNo}/managers",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DeptManagerResponse>> getDeptManagerByDeptNo(@RequestParam(value = "page",required = false,defaultValue = "0")Integer page,
                                                                         @RequestParam(value = "size",required = false,defaultValue = "10")Integer size,
                                                                         @PathVariable("deptNo")String deptNo){
        Page<DeptManagerResponse> deptManagerResponses = deptManagerService.getDeptManagerByDeptNo(deptNo,page,size);
        WebResponse<List<DeptManagerResponse>> webResponse = WebResponse.<List<DeptManagerResponse>>builder()
                .data(deptManagerResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(deptManagerResponses.getNumber())
                        .totalPage(deptManagerResponses.getTotalPages())
                        .size((int) deptManagerResponses.getTotalElements())
                        .build())
                .build();
        Link selfLink = linkTo(methodOn(DeptManagerController.class).getDeptManagerByDeptNo(page,size,deptNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @GetMapping(
            path = "/departments/{deptNo}/managers/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeptManagerResponse> getDeptManagerByDeptNoAndEmpNo(@PathVariable("deptNo")String deptNo,
                                                                           @PathVariable("empNo")Integer empNo){
        DeptManagerResponse deptManagerResponses = deptManagerService.getDeptManagerByDeptNoAndEmpNo(deptNo,empNo);
        WebResponse<DeptManagerResponse> webResponse = WebResponse.<DeptManagerResponse>builder()
                .data(deptManagerResponses)
                .build();
        Link selfLink = linkTo(methodOn(DeptManagerController.class).getDeptManagerByDeptNoAndEmpNo(deptNo,empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

    @DeleteMapping(
            path = "/departments/{deptNo}/managers/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteDeptManager(@PathVariable("deptNo")String deptNo,
                                                               @PathVariable("empNo")Integer empNo){
        deptManagerService.deleteDeptManager(deptNo,empNo);
        WebResponse<String> webResponse = WebResponse.<String>builder()
                .data("OK")
                .build();
        Link selfLink = linkTo(methodOn(DeptManagerController.class).deleteDeptManager(deptNo,empNo)).withSelfRel();
        webResponse.add(selfLink);
        return webResponse;
    }

//    @DeleteMapping(
//            path = "/departments/{deptNo}/managers",
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    public WebResponse<String> deleteDeptManagerByDeptNo(@PathVariable("deptNo")String deptNo){
//        deptManagerService.deleteDeptManagerByDeptNo(deptNo);
//        return WebResponse.<String>builder()
//                .data("OK")
//                .build();
//    }

    @PutMapping(
            path = "/departments/{deptNo}/managers/{empNo}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DeptManagerResponse> updateDeptManager(@PathVariable("deptNo")String deptNo,
                                                              @PathVariable("empNo")Integer empNo,
                                                              @RequestBody UpdateDeptManagerRequest request) throws ParseException {
        DeptManagerResponse deptManagerResponse = deptManagerService.updateDeptManager(deptNo,empNo,request);

        return WebResponse.<DeptManagerResponse>builder()
                .data(deptManagerResponse)
                .build();
    }

}
