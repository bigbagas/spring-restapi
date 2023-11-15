package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.EmployeeResponse;
import com.bagas.springrestapi.model.RegisterEmployeeRequest;
import com.bagas.springrestapi.model.UpdateEmployeeRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.repository.DepartmentRepository;
import com.bagas.springrestapi.repository.DeptEmpRepository;
import com.bagas.springrestapi.repository.DeptManagerRepository;
import com.bagas.springrestapi.repository.EmployeeRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DeptEmpRepository deptEmpRepository;

    @Autowired
    private DeptManagerRepository deptManagerRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws InterruptedException {

        deptEmpRepository.deleteAll();
        deptManagerRepository.deleteAll();
        departmentRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void registrasiEmployeeSuccess() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("Bagas");
        request.setLastName("Wiji");
        request.setGender("M");
        request.setHireDate("2022-09-21");

        // register success
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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNotNull(employeeTest);
            assertEquals(sdf.parse(request.getBirthDate()),employeeTest.getBirthDate());
            assertEquals(request.getFirstName(),employeeTest.getFirstName());
            assertEquals(request.getLastName(),employeeTest.getLastName());
            assertEquals(request.getGender(),employeeTest.getGender());
            assertEquals(sdf.parse(request.getHireDate()),employeeTest.getHireDate());
        });
    }

    @Test
    void registerEmployeeBadRequest() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("");
        request.setLastName("");
        request.setGender("M");
        request.setHireDate("2022-09-21");

        // first name & last name blank or null
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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

        RegisterEmployeeRequest request2 = new RegisterEmployeeRequest();
        request2.setBirthDate("1995-08-22");
        request2.setFirstName("Test");
        request2.setLastName("Test");
        request2.setGender("M");

        // hire date = null
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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

    }

    @Test
    void registerEmployeeDuplicate() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setEmpNo(1);
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Bagas");
        employee.setLastName("Bagas");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeeRepository.save(employee);

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("Bagas");
        request.setLastName("Bagas");
        request.setGender("M");
        request.setHireDate("2022-09-21");

        // employee duplicate
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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNotNull(employeeTest);

        });

    }

    @Test
    void registerEmployeeWrongDateFormat() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // wrong date format (format must yyyy-MM-dd)
        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("1995/08/22");
        request.setFirstName("Bagas");
        request.setLastName("Bagas");
        request.setGender("M");
        request.setHireDate("2022/09/21");

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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

        RegisterEmployeeRequest request2 = new RegisterEmployeeRequest();
        request2.setBirthDate("22-09-1992");
        request2.setFirstName("Bagas");
        request2.setLastName("Bagas");
        request2.setGender("M");
        request2.setHireDate("2022-09-21");

        mockMvc.perform(
                post("/api/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

    }

    @Test
    void registerEmployeeWrongGender() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // wrong gender (gender M of F)
        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("Bagas");
        request.setLastName("Bagas");
        request.setGender("Male");
        request.setHireDate("2022-09-21");

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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

        RegisterEmployeeRequest request2 = new RegisterEmployeeRequest();
        request2.setBirthDate("2020-09-21");
        request2.setFirstName("Bagas");
        request2.setLastName("Bagas");
        request2.setGender("L");
        request2.setHireDate("2022-09-21");

        mockMvc.perform(
                post("/api/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

    }

    @Test
    void registerEmployeeBirthDateMoreThanHireDate() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // birth date more than hire date
        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("2024-08-22");
        request.setFirstName("Bagas");
        request.setLastName("Bagas");
        request.setGender("M");
        request.setHireDate("2022-09-21");

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

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
            assertNull(employeeTest);

        });

        // birth date more than hire date
        RegisterEmployeeRequest request2 = new RegisterEmployeeRequest();
        request2.setBirthDate("2017-08-11");
        request2.setFirstName("Bagas");
        request2.setLastName("Bagas");
        request2.setGender("M");
        request2.setHireDate("2014-09-21");

        mockMvc.perform(
                post("/api/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Employee employeeTest = employeeRepository.findFirstByFirstName(request.getFirstName()).orElse(null);
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
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("Nanda");
        request.setLastName("Wahyu");
        request.setGender("F");
        request.setHireDate("2022-10-21");

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
            assertNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(employeeTest);
            assertEquals(sdf.parse(request.getBirthDate()),employeeTest.getBirthDate());
            assertEquals(request.getFirstName(),employeeTest.getFirstName());
            assertEquals(request.getLastName(),employeeTest.getLastName());
            assertEquals(request.getGender(),employeeTest.getGender());
            assertEquals(sdf.parse(request.getHireDate()),employeeTest.getHireDate());
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
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        // first name & last name too long
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("Nandaaaaaaaaaaaaaaaaaaaa");
        request.setLastName("Wahyuuuuuuuuuuuuuuuuuu");
        request.setGender("F");
        request.setHireDate("2022-10-21");

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

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });

        // wrong gender
        UpdateEmployeeRequest request2 = new UpdateEmployeeRequest();
        request2.setBirthDate("1995-08-22");
        request2.setFirstName("Nanda");
        request2.setLastName("Wahyu");
        request2.setGender("X");
        request2.setHireDate("2022-10-21");

        mockMvc.perform(
                put("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });

        // wrong gender
        UpdateEmployeeRequest request3 = new UpdateEmployeeRequest();
        request3.setBirthDate("1995-08-22");
        request3.setFirstName("Nanda");
        request3.setLastName("Wahyu");
        request3.setGender("FEMALE");
        request3.setHireDate("2022-10-21");

        mockMvc.perform(
                put("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request3))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void updateEmployeeWrongDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        // wrong date format
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setBirthDate("1995/08/22");
        request.setFirstName("Nandaa");
        request.setLastName("Wahyuu");
        request.setGender("F");
        request.setHireDate("2022/10/21");

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

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });

        // date birth more than hire date
        UpdateEmployeeRequest request2 = new UpdateEmployeeRequest();
        request2.setBirthDate("2025-08-22");
        request2.setFirstName("Nandaa");
        request2.setLastName("Wahyuu");
        request2.setGender("F");
        request2.setHireDate("2022/10/21");

        mockMvc.perform(
                put("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request2))
        ).andExpectAll(status().isBadRequest()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
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
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        RegisterEmployeeRequest request = new RegisterEmployeeRequest();
        request.setBirthDate("1995-08-22");
        request.setFirstName("Nanda");
        request.setLastName("Wahyu");
        request.setGender("F");
        request.setHireDate("2022-10-21");

        // not found ( there is no empNo = 0)
        mockMvc.perform(
                put("/api/employees/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getData());
            assertNotNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void employeeByEmpNoIsSuccess() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        // get by emp no success
        mockMvc.perform(
                get("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(employeeTest);
            assertEquals(employee.getBirthDate(),employeeTest.getBirthDate());
            assertEquals(employee.getFirstName(),employeeTest.getFirstName());
            assertEquals(employee.getLastName(),employeeTest.getLastName());
            assertEquals(employee.getGender(),employeeTest.getGender());
            assertEquals(employee.getHireDate(),employeeTest.getHireDate());
        });
    }

    @Test
    void employeeByEmpNoIsNotFound() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        // emp no not found
        mockMvc.perform(
                get("/api/employees/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebResponse<EmployeeResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
        });
    }

    @Test
    void deleteEmployeeSuccess() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        // delete success
        mockMvc.perform(
                delete("/api/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertEquals("OK",response.getData());
            assertNull(response.getErrors());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertNull(employeeTest);

        });
    }

    @Test
    void deleteEmployeeNotFound() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1998-09-11"));
        employee.setFirstName("Bagas");
        employee.setLastName("Anwar");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2022-09-21"));
        employeeRepository.save(employee);
        System.out.println(employee.getEmpNo());

        // delete not found (empNo = 0 is not found)
        mockMvc.perform(
                delete("/api/employees/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Employee employeeTest = employeeRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(employeeTest);

        });
    }

    @Test
    void findAllEmployeeSuccess() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1998-09-11"));
            employee.setFirstName("Bagas");
            employee.setLastName("Anwar");
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2022-09-21"));
            employeeRepository.save(employee);
            System.out.println(employee.getEmpNo());
        }

        // get all success
        mockMvc.perform(
                get("/api/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());
        });
    }

    @Test
    void searchEmployeeSuccess() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1998-09-11"));
            employee.setFirstName("Bagas");
            employee.setLastName("Nur");
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2022-09-21"));
            employeeRepository.save(employee);
            System.out.println(employee.getEmpNo());
        }

        //searching success
        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("2000-01-12"));
            employee.setFirstName("Nurul");
            employee.setLastName("Aisyah");
            employee.setGender("F");
            employee.setHireDate(sdf.parse("2023-08-22"));
            employeeRepository.save(employee);
            System.out.println(employee.getEmpNo());
        }


        mockMvc.perform(
                get("/api/employees/search")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(20,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(200,response.getPaging().getSize());


        });

        mockMvc.perform(
                get("/api/employees/search?keyword=Nur")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(20,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(200,response.getPaging().getSize());


        });

        mockMvc.perform(
                get("/api/employees/search?keyword=Bag")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });

        mockMvc.perform(
                get("/api/employees/search?keyword=Bag&page=3")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(3,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });

        mockMvc.perform(
                get("/api/employees/search?keyword=Bag&page=3&size=20")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(20,response.getData().size());
            assertEquals(5,response.getPaging().getTotalPage());
            assertEquals(3,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });

        mockMvc.perform(
                get("/api/employees/search?keyword=bag")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });


        mockMvc.perform(
                get("/api/employees/search?keyword=BAG")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });

        mockMvc.perform(
                get("/api/employees/search?keyword=bAgA")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });


    }

    @Test
    void searchEmployeeNotFound() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1998-09-11"));
            employee.setFirstName("Bagas");
            employee.setLastName("Anwar");
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2022-09-21"));
            employeeRepository.save(employee);
            System.out.println(employee.getEmpNo());
        }


        // search nurul not found
        mockMvc.perform(
                get("/api/employees/search?keyword=Nurul")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(status().isOk()
        ).andDo(result -> {
            WebResponse<List<EmployeeResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(0,response.getPaging().getSize());


        });
    }

}
