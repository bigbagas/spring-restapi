package com.bagas.springrestapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> extends RepresentationModel<WebResponse<T>> {

    private T data;
    private String errors;
    private PagingResponse paging;


}
