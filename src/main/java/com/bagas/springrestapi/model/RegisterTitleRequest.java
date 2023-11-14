package com.bagas.springrestapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterTitleRequest {

    @NotNull
    @Column(length = 11)
    private Integer empNo;

    @NotBlank
    private String title;

    @NotBlank
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",message = "date format must yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private String fromDate;

    @NotBlank
    @Pattern(regexp = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$",message = "date format must yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private String toDate;
}
