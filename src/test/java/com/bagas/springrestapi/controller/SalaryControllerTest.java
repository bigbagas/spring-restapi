package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Salary;
import com.bagas.springrestapi.entity.Title;
import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.bagas.springrestapi.repository.SalaryRepository;
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
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SalaryControllerTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        salaryRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void registrasiSalarySuccess() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(1_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        mockMvc.perform(
                post("/api/salaries")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());
            assertEquals("OK",response.getData());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(salaryTest);
            assertEquals(request.getSalary(),salaryTest.getSalary());
            assertEquals(sdf.parse(request.getFromDate()),salaryTest.getFromDate());
            assertEquals(sdf.parse(request.getToDate()),salaryTest.getToDate());
        });

    }

    @Test
    void registrasiSalaryBadRequest() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        // date format is wrong
        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(1_000_000);
        request.setFromDate("2020/09/11");
        request.setToDate("2021/08/23");

        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNull(response.getData());
            assertNotNull(response.getErrors());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNull(salaryTest);
        });

    }

    @Test
    void registrasiSalaryDuplicate() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        // test duplicate
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

    }

    @Test
    void updateSalarySuccess() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        UpdateSalaryRequest updateSalaryRequest = new UpdateSalaryRequest();
        updateSalaryRequest.setSalary(2_000_000);
        updateSalaryRequest.setFromDate("2021-09-11");
        updateSalaryRequest.setToDate("2022-10-23");

        // test update success
        mockMvc.perform(
                put("/api/salaries/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateSalaryRequest))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(salaryTest);
            assertEquals(updateSalaryRequest.getSalary(),salaryTest.getSalary());
        });

    }

    @Test
    void updateSalaryNotFound() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        Employee employee2 = new Employee();
        employee2.setBirthDate(sdf.parse("1995-08-22"));
        employee2.setFirstName("Test2");
        employee2.setLastName("Test2");
        employee2.setGender("M");
        employee2.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee2);


        UpdateSalaryRequest updateSalaryRequest = new UpdateSalaryRequest();
        updateSalaryRequest.setSalary(1_000_000);
        updateSalaryRequest.setFromDate("2021-09-11");
        updateSalaryRequest.setToDate("2022-10-23");

        // test update: emp no is not found in salary
        mockMvc.perform(
                put("/api/salaries/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateSalaryRequest))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotEquals(updateSalaryRequest.getSalary(),salaryTest.getSalary());
        });

        //emp no is not found in employee
        mockMvc.perform(
                put("/api/salaries/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateSalaryRequest))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotEquals(updateSalaryRequest.getSalary(),salaryTest.getSalary());
        });

    }

    @Test
    void updateSalaryBadRequest() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest requestInsert = new RegisterSalaryRequest();
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setSalary(2_000_000);
        requestInsert.setFromDate("2020-09-11");
        requestInsert.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateSalaryRequest updateSalaryRequest = new UpdateSalaryRequest();
        updateSalaryRequest.setSalary(1_000_000);
        updateSalaryRequest.setFromDate("2021/09/11");
        updateSalaryRequest.setToDate("2022/10/23");

        // test update: wrong date format
        mockMvc.perform(
                put("/api/salaries/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateSalaryRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotEquals(updateSalaryRequest.getSalary(),salaryTest.getSalary());
        });

        //test update: wrong date format
        mockMvc.perform(
                put("/api/salaries/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateSalaryRequest))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotEquals(updateSalaryRequest.getSalary(),salaryTest.getSalary());
        });

        UpdateSalaryRequest updateSalaryRequest2 = new UpdateSalaryRequest();
        updateSalaryRequest2.setSalary(1_000_000);
        updateSalaryRequest2.setFromDate("2021/09/11");
        updateSalaryRequest2.setToDate("2022/10/23");

        // test update: salary = 0
        mockMvc.perform(
                put("/api/salaries/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(updateSalaryRequest2))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotEquals(updateSalaryRequest.getSalary(),salaryTest.getSalary());
        });

    }

    @Test
    void salaryGetByEmpNoIsSuccess() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        // test get by emp no:  success
        mockMvc.perform(
                get("/api/salaries/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(salaryTest);
        });

    }

    @Test
    void salaryGetByEmpNoIsNotFound() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        Employee employee2 = new Employee();
        employee2.setBirthDate(sdf.parse("1995-08-22"));
        employee2.setFirstName("Test");
        employee2.setLastName("Test");
        employee2.setGender("M");
        employee2.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee2);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        // test emp no is not found in salary
        mockMvc.perform(
                get("/api/salaries/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

        });

        // test emp no is not found in salary
        mockMvc.perform(
                get("/api/salaries/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<SalaryResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

        });


    }

    @Test
    void getAllSalarySuccess() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 100; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }
        for (int j = 0; j < 100; j++) {
            RegisterSalaryRequest request = new RegisterSalaryRequest();
            request.setEmpNo(j+1);
            request.setSalary(2_000_000+j);
            request.setFromDate("2020-09-11");
            request.setToDate("2021-08-23");

            //Insert salary
            mockMvc.perform(
                    post("/api/salaries")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
            );
        }


        // test get all salary
        mockMvc.perform(
                get("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SalaryResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10, response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });

        // test get all salary in page 2
        mockMvc.perform(
                get("/api/salaries?page=2")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SalaryResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10, response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(2,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });

        // test get all salary in page 4 and size 20
        mockMvc.perform(
                get("/api/salaries?page=4&size=20")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SalaryResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(20, response.getData().size());
            assertEquals(5,response.getPaging().getTotalPage());
            assertEquals(4,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());


        });



    }

    @Test
    void searchSalarySuccess() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 99; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }

        for (int j = 0; j < 50; j++) {
            RegisterSalaryRequest request = new RegisterSalaryRequest();
            request.setEmpNo(j+1);
            request.setSalary(2_000_000);
            request.setFromDate("2020-09-11");
            request.setToDate("2021-08-23");

            //Insert salary
            mockMvc.perform(
                    post("/api/salaries")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
            );
        }

        for (int j = 0; j < 49; j++) {
            RegisterSalaryRequest request = new RegisterSalaryRequest();
            request.setEmpNo(j+51);
            request.setSalary(1_000_000);
            request.setFromDate("2020-09-11");
            request.setToDate("2021-08-23");

            //Insert salary
            mockMvc.perform(
                    post("/api/salaries")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
            );
        }


        // search without keyword (will get all data)
        mockMvc.perform(
                get("/api/salaries/search")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SalaryResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10, response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(99,response.getPaging().getSize());


        });


        // search keyword = 1000000 and in page 4
        mockMvc.perform(
                get("/api/salaries/search?keyword=1000000&page=4")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SalaryResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(9, response.getData().size());
            assertEquals(5,response.getPaging().getTotalPage());
            assertEquals(4,response.getPaging().getCurrentPage());
            assertEquals(49,response.getPaging().getSize());


        });
    }

    @Test
    void searchSalaryNotFound() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }

        for (int j = 0; j < 10; j++) {
            RegisterSalaryRequest request = new RegisterSalaryRequest();
            request.setEmpNo(j+1);
            request.setSalary(2_000_000);
            request.setFromDate("2020-09-11");
            request.setToDate("2021-08-23");

            //Insert salary
            mockMvc.perform(
                    post("/api/salaries")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request))
            );
        }

        // search not found
        mockMvc.perform(
                get("/api/salaries/search?keyword=500000")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<SalaryResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(0, response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(0,response.getPaging().getSize());


        });


    }

    @Test
    void salaryDeleteSuccess() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        // test get by emp no:  success
        mockMvc.perform(
                delete("/api/salaries/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());
            assertEquals("OK",response.getData());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNull(salaryTest);
        });

    }

    @Test
    void salaryDeleteNotFound() throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        Employee employee2 = new Employee();
        employee2.setBirthDate(sdf.parse("1995-08-22"));
        employee2.setFirstName("Test2");
        employee2.setLastName("Test2");
        employee2.setGender("M");
        employee2.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee2);

        RegisterSalaryRequest request = new RegisterSalaryRequest();
        request.setEmpNo(employee.getEmpNo());
        request.setSalary(2_000_000);
        request.setFromDate("2020-09-11");
        request.setToDate("2021-08-23");

        //Insert salary
        mockMvc.perform(
                post("/api/salaries")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        );

        // emp no not found in salary
        mockMvc.perform(
                delete("/api/salaries/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(salaryTest);
        });

        // emp no not found in employee
        mockMvc.perform(
                delete("/api/salaries/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            Salary salaryTest = salaryRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(salaryTest);
        });

    }
}
