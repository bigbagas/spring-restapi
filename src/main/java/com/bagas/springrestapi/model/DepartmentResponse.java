package com.bagas.springrestapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DepartmentResponse extends RepresentationModel<DepartmentResponse>{

    private String deptNo;

    private String deptName;
}
