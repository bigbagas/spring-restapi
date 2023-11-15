package com.bagas.springrestapi.model;

import com.bagas.springrestapi.entity.Title;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TitleResponse extends RepresentationModel<Title> {

    private Integer empNo;

    private String title;

    @Temporal(TemporalType.DATE)
    private Date fromDate;

    @Temporal(TemporalType.DATE)
    private Date toDate;
}
