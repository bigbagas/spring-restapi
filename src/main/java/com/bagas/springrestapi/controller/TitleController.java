package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.model.RegisterTitleRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.service.TitleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public WebResponse<String> regiterTitle(@RequestBody RegisterTitleRequest request){
        titleService.registerTitle(request);
        return WebResponse.<String>builder()
                .data("OK")
                .build();
    }
}
