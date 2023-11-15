package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.model.DepartmentResponse;
import com.bagas.springrestapi.model.RegisterDepartmentRequest;
import com.bagas.springrestapi.model.UpdateDepartmentRequest;
import com.bagas.springrestapi.model.WebResponse;
import com.bagas.springrestapi.repository.DepartmentRepository;
import com.bagas.springrestapi.repository.DeptEmpRepository;
import com.bagas.springrestapi.repository.DeptManagerRepository;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DepartmentControllerTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DeptEmpRepository deptEmpRepository;

    @Autowired
    private DeptManagerRepository deptManagerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        deptEmpRepository.deleteAll();
        deptManagerRepository.deleteAll();
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
    }

    @Test
    void registerDepartmentSuccess() throws Exception{

        RegisterDepartmentRequest request = new RegisterDepartmentRequest();
        request.setDeptNo("TEST");
        request.setDeptName("TEST");

        //success
        mockMvc.perform(
                post("/api/departments")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertEquals("OK",response.getData());
            assertNull(response.getErrors());

            Department departmentTest = departmentRepository.findById(request.getDeptNo()).orElse(null);
            assertNotNull(departmentTest);
            assertEquals(request.getDeptNo(),departmentTest.getDeptNo());
            assertEquals(request.getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void registerDepartmentBadRequest() throws Exception{

        RegisterDepartmentRequest requestDeptNoTooLong = new RegisterDepartmentRequest();
        requestDeptNoTooLong.setDeptNo("TEST. This Dept No is too long");
        requestDeptNoTooLong.setDeptName("TEST");

        // dept no is too long
        mockMvc.perform(
                post("/api/departments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNoTooLong))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            Department departmentTest = departmentRepository.findById(requestDeptNoTooLong.getDeptNo()).orElse(null);
            assertNull(departmentTest);
        });

        // dept name is too long
        RegisterDepartmentRequest requestDeptNameMoreThan40 = new RegisterDepartmentRequest();
        requestDeptNameMoreThan40.setDeptNo("TEST");
        requestDeptNameMoreThan40.setDeptName("TEST. This dept name is more than 40 character. This dept name is more than 40 character");

        mockMvc.perform(
                post("/api/departments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNameMoreThan40))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            Department departmentTest = departmentRepository.findById(requestDeptNoTooLong.getDeptNo()).orElse(null);
            assertNull(departmentTest);
        });

        // dept name is null
        RegisterDepartmentRequest requestDeptNameNull = new RegisterDepartmentRequest();
        requestDeptNameNull.setDeptNo("TEST");

        mockMvc.perform(
                post("/api/departments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNameNull))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            Department departmentTest = departmentRepository.findById(requestDeptNoTooLong.getDeptNo()).orElse(null);
            assertNull(departmentTest);
        });
        
        
    }

    @Test
    void registerDepartmentDuplicate() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);

        RegisterDepartmentRequest request = new RegisterDepartmentRequest();
        request.setDeptNo("TEST");
        request.setDeptName("TEST");

        // check duplicate
        mockMvc.perform(
                post("/api/departments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Department departmentTest = departmentRepository.findById(department.getDeptNo()).orElse(null);
            assertNotNull(departmentTest);
            assertEquals(request.getDeptNo(),departmentTest.getDeptNo());
            assertEquals(request.getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void getDepartmentByDeptNoIsSuccess() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);
        System.out.println(department.getDeptNo());

        // get by dept no is success
        mockMvc.perform(
                get("/api/departments/TEST")
                        .accept(MediaType.APPLICATION_JSON_VALUE)

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DepartmentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            System.out.println("error ="+response.getErrors());

            assertEquals("TEST",response.getData().getDeptNo());
            assertEquals("TEST",response.getData().getDeptName());

            Department departmentTest = departmentRepository.findById(department.getDeptNo()).orElse(null);
            assertNotNull(departmentTest);
            assertEquals(response.getData().getDeptNo(),departmentTest.getDeptNo());
            assertEquals(response.getData().getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void getDepartmentByDeptNoIsNotFound() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);
        System.out.println(department.getDeptNo());

        // not found (deptNo = NOTFOUND cannot be found Departments)
        mockMvc.perform(
                get("/api/departments/NOTFOUND")
                        .accept(MediaType.APPLICATION_JSON_VALUE)

        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DepartmentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Department departmentTest = departmentRepository.findById("NOTFOUND").orElse(null);
            assertNull(departmentTest);
        });
    }

    @Test
    void updateDepartmentSuccess() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);
        System.out.println(department.getDeptNo());

        UpdateDepartmentRequest request = new UpdateDepartmentRequest();
        request.setDeptName("NEW DEPT");
        System.out.println("req "+request.getDeptName());

        // update success
        mockMvc.perform(
                put("/api/departments/TEST")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))


        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DepartmentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());

            assertNull(response.getErrors());

            Department departmentTest = departmentRepository.findById("TEST").orElse(null);
            System.out.println(departmentTest.getDeptName());
            assertNotNull(departmentTest);
            assertEquals(request.getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void updateDepartmentNotFound() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);
        System.out.println(department.getDeptNo());

        UpdateDepartmentRequest request = new UpdateDepartmentRequest();
        request.setDeptName("NEW DEPT");

        // not found (deptNo = NOTFOUND cannot be found Departments)
        mockMvc.perform(
                put("/api/departments/NOTFOUND")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DepartmentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Department departmentTest = departmentRepository.findById("TEST").orElse(null);
            assertNotNull(departmentTest);
            assertEquals(department.getDeptNo(),departmentTest.getDeptNo());
            assertEquals(department.getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void updateDepartmentBadRequest() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);

        // update. deptName is too long (more than 40)
        UpdateDepartmentRequest requestTooLong = new UpdateDepartmentRequest();
        requestTooLong.setDeptName("NEW DEPT is too long. NEW DEPT is too long. NEW DEPT is too long. NEW DEPT is too long");

        mockMvc.perform(
                put("/api/departments/NOTFOUND")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestTooLong))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<DepartmentResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            Department departmentTest = departmentRepository.findById("TEST").orElse(null);
            assertNotNull(departmentTest);
            assertEquals(department.getDeptNo(),departmentTest.getDeptNo());
            assertEquals(department.getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void deleteDepartmentSuccess() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);

        //delete success
        mockMvc.perform(
                delete("/api/departments/TEST")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertEquals("OK",response.getData());

            Department departmentTest = departmentRepository.findById("TEST").orElse(null);
            assertNull(departmentTest);
        });
    }

    @Test
    void deleteDepartmentNotFound() throws Exception{
        Department department = new Department();
        department.setDeptNo("TEST");
        department.setDeptName("TEST");
        departmentRepository.save(department);

        // not found (deptNo = NOTFOUND cannot be found Departments)
        mockMvc.perform(
                delete("/api/departments/NOTFOUND")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            Department departmentTest = departmentRepository.findById("TEST").orElse(null);
            assertNotNull(departmentTest);
            assertEquals(department.getDeptNo(),departmentTest.getDeptNo());
            assertEquals(department.getDeptName(),departmentTest.getDeptName());
        });
    }

    @Test
    void gatAllDepartmentSuccess() throws Exception{
        for (int i = 0; i < 100; i++) {
            Department department = new Department();
            department.setDeptNo(String.valueOf(i));
            department.setDeptName("TEST"+1);
            departmentRepository.save(department);

        }

        // success get all Departments
        mockMvc.perform(
                get("/api/departments")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());
        });
    }

    @Test
    void searchDepartmentNotFound() throws Exception{
        for (int i = 0; i < 100; i++) {
            Department department = new Department();
            department.setDeptNo("K"+i);
            department.setDeptName("TEST"+i);
            departmentRepository.save(department);

        }

        // not found (dept name with keyword = NOTFOUND cannot be found Departments)
        mockMvc.perform(
                get("/api/departments/search?keyword=NOTFOUND")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(0,response.getData().size());
            assertEquals(0,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(0,response.getPaging().getSize());
        });

    }

    @Test
    void searchDepartmentSuccess() throws Exception{
        for (int i = 0; i < 100; i++) {
            Department department = new Department();
            department.setDeptNo("K"+i);
            department.setDeptName("TEST"+i);
            departmentRepository.save(department);

        }

        for (int i = 0; i < 100; i++) {
            Department department1 = new Department();
            department1.setDeptNo("D"+i);
            department1.setDeptName("DATA TEST "+i);
            departmentRepository.save(department1);

        }

        // success. if keyword is null, it will get all data
        mockMvc.perform(
                get("/api/departments/search?keyword")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(20,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(200,response.getPaging().getSize());
        });

        // search K is success
        mockMvc.perform(
                get("/api/departments/search?keyword=K")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());
        });

        // success.
        mockMvc.perform(
                get("/api/departments/search?keyword=DAta")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(10,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/departments/search?keyword=TEST")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(20,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(200,response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/departments/search?keyword=TEST&page=6&size=10")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(20,response.getPaging().getTotalPage());
            assertEquals(6,response.getPaging().getCurrentPage());
            assertEquals(200,response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/departments/search?keyword=DAta&page=3&size=20")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<WebResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(20,response.getData().size());
            assertEquals(5,response.getPaging().getTotalPage());
            assertEquals(3,response.getPaging().getCurrentPage());
            assertEquals(100,response.getPaging().getSize());
        });


    }

}
