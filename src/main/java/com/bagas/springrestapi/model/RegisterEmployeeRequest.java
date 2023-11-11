package com.bagas.springrestapi.model;

import com.bagas.springrestapi.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterEmployeeRequest {

    @NotNull
    @Past
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @NotBlank
    @Size(max = 14, message = "firstName length must be between 0 and 16")
    private String firstName;

    @NotBlank(message = "lastName cannot be empty")
    @Size(max = 16)
    private String lastName;

    @NotBlank
    @Size(max = 1,message = "Gender must either M or F")
    @Pattern(regexp = "[MFmf]+", message = "Gender must either M or F")
    private String gender;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date hireDate;
}
