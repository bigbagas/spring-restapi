package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.EmployeeResponse;
import com.bagas.springrestapi.model.RegisterEmployeeRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.repository.EmployeesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        employeesRepository.deleteAll();
    }

    @Test
    void registrasiEmployeeSuccess() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate(sdf.parse("1995-08-22"));
        request.setFirstName("Bagas");
        request.setLastName("Wiji");
        request.setGender("M");
        request.setHireDate(sdf.parse("2022-09-21"));

        mockMvc.perform(
                post("/api/employees")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertEquals("OK",response.getData());
            assertNull(response.getErrors());

            Employee employeeTest = employeesRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNotNull(employeeTest);
            assertEquals(request.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(request.getFirstName(),employeeTest.getFirstName());
            assertEquals(request.getLastName(),employeeTest.getLastName());
            assertEquals(request.getGender(),employeeTest.getGender());
            assertEquals(request.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void registerEmployeeBadRequest() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate(sdf.parse("1995-08-22"));
        request.setFirstName("");
        request.setLastName("");
        request.setGender("M");
        request.setHireDate(sdf.parse("2022-09-21"));

        mockMvc.perform(
                post("/api/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Employee employeeTest = employeesRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

    }

    @Test
    void updateEmployeeSuccess() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeesRepository.save(employee);
        System.out.println(employee.getEmpNo());

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate(sdf.parse("1995-08-22"));
        request.setFirstName("Nanda");
        request.setLastName("Wahyu");
        request.setGender("F");
        request.setHireDate(sdf.parse("2022-10-21"));

        mockMvc.perform(
                put("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            System.out.println(response.getData().getBirthDate());
            System.out.println(request.getBirthDate());
            assertEquals(response.getData().getEmpNo(),employee.getEmpNo());
            assertEquals(response.getData().getBirthDate(),request.getBirthDate());
            assertEquals(response.getData().getFirstName(),request.getFirstName());
            assertEquals(response.getData().getLastName(),request.getLastName());
            assertEquals(response.getData().getGender(),request.getGender());
            assertEquals(response.getData().getHireDate(),request.getHireDate());
            assertNull(response.getErrors());

            Employee employeeTest = employeesRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(employeeTest);
            assertEquals(request.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(request.getFirstName(),employeeTest.getFirstName());
            assertEquals(request.getLastName(),employeeTest.getLastName());
            assertEquals(request.getGender(),employeeTest.getGender());
            assertEquals(request.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void updateEmployeeBadRequest() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeesRepository.save(employee);
        System.out.println(employee.getEmpNo());

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate(sdf.parse("1995-08-22"));
        request.setFirstName("Nandaaaaaaaaaaaaaaaaaaaa");
        request.setLastName("Wahyuuuuuuuuuuuuuuuuuu");
        request.setGender("F");
        request.setHireDate(sdf.parse("2022-10-21"));

        mockMvc.perform(
                put("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());

            Employee employeeTest = employeesRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void updateEmployeeNotFound() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeesRepository.save(employee);
        System.out.println(employee.getEmpNo());

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate(sdf.parse("1995-08-22"));
        request.setFirstName("Nanda");
        request.setLastName("Wahyu");
        request.setGender("F");
        request.setHireDate(sdf.parse("2022-10-21"));

        mockMvc.perform(
                put("/api/employees/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());

            Employee employeeTest = employeesRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void employeeByEmpIdSuccess() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeesRepository.save(employee);
        System.out.println(employee.getEmpNo());


        mockMvc.perform(
                get("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            Employee employeeTest = employeesRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(employeeTest);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void employeeByEmpIdNotFound() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeesRepository.save(employee);
        System.out.println(employee.getEmpNo());


        mockMvc.perform(
                get("/api/employees/1")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
        });
    }

}
