package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptManager;
import com.bagas.springrestapi.entity.DeptManager;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.*;
import com.bagas.springrestapi.repository.DepartmentRepository;
import com.bagas.springrestapi.repository.DeptManagerRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeptManagerControllerTest {

    @Autowired
    private DeptManagerRepository DeptManagerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DeptManagerRepository deptManagerRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        DeptManagerRepository.deleteAll();
        deptManagerRepository.deleteAll();
        departmentRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void registerDeptManagerSuccess() throws Exception{
        Department department = new Department();
        department.setDeptNo("Test");
        department.setDeptName("Test");
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest request = new RegisterDeptManagerRequest();
        request.setDeptNo(department.getDeptNo());
        request.setEmpNo(employee.getEmpNo());
        request.setFromDate("2020-09-21");
        request.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
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
            assertEquals("OK", response.getData());
            assertNotNull(response.getLinks());

            DeptManager deptManager = deptManagerRepository.findById(request.getEmpNo()).orElse(null);
            assertNotNull(deptManager);
            assertEquals(request.getDeptNo(),deptManager.getDepartment().getDeptNo());
            assertEquals(request.getEmpNo(),deptManager.getEmpNo());
            assertEquals(sdf.parse(request.getFromDate()),deptManager.getFromDate());
            assertEquals(sdf.parse(request.getToDate()),deptManager.getToDate());

        });
    }

    @Test
    void registerDeptManagerBadRequest() throws Exception{
        Department department = new Department();
        department.setDeptNo("Test");
        department.setDeptName("Test");
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestDeptNoTooLong = new RegisterDeptManagerRequest();
        requestDeptNoTooLong.setDeptNo("Dept No is too long. Dept No is too long.");
        requestDeptNoTooLong.setEmpNo(1);
        requestDeptNoTooLong.setFromDate("2020-09-21");
        requestDeptNoTooLong.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNoTooLong))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(requestDeptNoTooLong.getEmpNo()).orElse(null);
            assertNull(DeptManagerTest);

        });
    }

    @Test
    void registerDeptManagerNotFound() throws Exception{
        Department department = new Department();
        department.setDeptNo("Test");
        department.setDeptName("Test");
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestDeptNoIsNotFound = new RegisterDeptManagerRequest();
        requestDeptNoIsNotFound.setDeptNo("Dept");
        requestDeptNoIsNotFound.setEmpNo(1);
        requestDeptNoIsNotFound.setFromDate("2020-09-21");
        requestDeptNoIsNotFound.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNoIsNotFound))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(requestDeptNoIsNotFound.getEmpNo()).orElse(null);
            assertNull(DeptManagerTest);

        });

        RegisterDeptManagerRequest requestEmpNoIsNotFound = new RegisterDeptManagerRequest();
        requestEmpNoIsNotFound.setDeptNo("Test");
        requestEmpNoIsNotFound.setEmpNo(33);
        requestEmpNoIsNotFound.setFromDate("2020-09-21");
        requestEmpNoIsNotFound.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestEmpNoIsNotFound))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(requestDeptNoIsNotFound.getEmpNo()).orElse(null);
            assertNull(DeptManagerTest);

        });
    }

    @Test
    void registerDeptManagerAlreadyRegister() throws Exception{
        Department department = new Department();
        department.setDeptNo("Test");
        department.setDeptName("Test");
        departmentRepository.save(department);

        Department department2 = new Department();
        department2.setDeptNo("no2");
        department2.setDeptName("Test No 2");
        departmentRepository.save(department2);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        RegisterDeptManagerRequest requestAlreadyRegisterInThisDept = new RegisterDeptManagerRequest();
        requestAlreadyRegisterInThisDept.setDeptNo(department.getDeptNo());
        requestAlreadyRegisterInThisDept.setEmpNo(employee.getEmpNo());
        requestAlreadyRegisterInThisDept.setFromDate("2020-09-21");
        requestAlreadyRegisterInThisDept.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestAlreadyRegisterInThisDept))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

        });

        RegisterDeptManagerRequest requestAlreadyRegisterInOtherDept = new RegisterDeptManagerRequest();
        requestAlreadyRegisterInOtherDept.setDeptNo(department2.getDeptNo());
        requestAlreadyRegisterInOtherDept.setEmpNo(employee.getEmpNo());
        requestAlreadyRegisterInOtherDept.setFromDate("2020-09-21");
        requestAlreadyRegisterInOtherDept.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestAlreadyRegisterInOtherDept))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

        });

    }

    @Test
    void getDeptManagerByDeptNoIsSuccess() throws Exception{

            Department department = new Department();
            department.setDeptNo("B16");
            department.setDeptName("Test"+1);
            departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 20; i++) {
            System.out.println("hitung "+i);
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }


        for (int j = 0; j < 20; j++) {

            System.out.println("mulai "+j);

            RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate("2020-09-21");
            requestInsert.setToDate("2023-09-21");

            mockMvc.perform(
                    post("/api/departments/managers")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );


        }


        mockMvc.perform(
                get("/api/departments/B16/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<DeptManagerResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertNotNull(response.getLinks());
            assertEquals(10,response.getData().size());
            assertEquals(2,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(20,response.getPaging().getSize());


        });


    }



    @Test
    void getDeptManagerByDeptNoIsNotFound() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 20; i++) {
            System.out.println("hitung "+i);
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }


        for (int j = 0; j < 20; j++) {

            System.out.println("mulai "+j);

            RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate("2020-09-21");
            requestInsert.setToDate("2023-09-21");

            mockMvc.perform(
                    post("/api/departments/managers")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );


        }


        mockMvc.perform(
                get("/api/departments/Z90/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<List<DeptManagerResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

        });


    }

    @Test
    void getAllDeptManagerByDeptNoIsSuccess() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 20; i++) {
            System.out.println("hitung "+i);
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }


        for (int j = 0; j < 20; j++) {

            System.out.println("mulai "+j);

            RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate("2020-09-21");
            requestInsert.setToDate("2023-09-21");

            mockMvc.perform(
                    post("/api/departments/managers")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );


        }


        mockMvc.perform(
                get("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<DeptManagerResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertNotNull(response.getLinks());
            assertEquals(10,response.getData().size());
            assertEquals(2,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(20,response.getPaging().getSize());


        });


    }

    @Test
    void getDeptManagerByDeptNoAndEmpNoIsSuccess() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        mockMvc.perform(
                get("/api/departments/B16/managers/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());
            assertNotNull(response.getLinks());
        });


    }

    @Test
    void getDeptManagerByDeptNoAndEmpNoIsNotFound() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        Department department2 = new Department();
        department2.setDeptNo("B166");
        department2.setDeptName("Test"+2);
        departmentRepository.save(department);

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
        employee2.setGender("2");
        employee2.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee2);

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        //
        mockMvc.perform(
                get("/api/departments/B16/managers/5")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

        mockMvc.perform(
                get("/api/departments/B22/managers/5")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

        mockMvc.perform(
                get("/api/departments/"+department2.getDeptNo()+"/managers/5")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

        mockMvc.perform(
                get("/api/departments/"+department2.getDeptNo()+"/managers/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });



    }

    @Test
    void updateDeptManagerSuccess() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        Department department2 = new Department();
        department2.setDeptNo("ZX99");
        department2.setDeptName("Test"+2);
        departmentRepository.save(department2);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateDeptManagerRequest request = new UpdateDeptManagerRequest();
        request.setDeptNo(department2.getDeptNo());
        request.setFromDate("2019-04-24");
        request.setToDate("2023-01-01");


        //
        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/managers/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            System.out.println("/api/departments/"+department.getDeptNo()+"/managers/"+employee.getEmpNo());
            System.out.println("res "+response);
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());
            assertNotNull(response.getLinks());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            System.out.println(DeptManagerTest);
            assertNotNull(DeptManagerTest);
            assertEquals(DeptManagerTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertEquals(DeptManagerTest.getFromDate(),sdf.parse(request.getFromDate()));
            assertEquals(DeptManagerTest.getToDate(),sdf.parse(request.getToDate()));
        });

    }

    @Test
    void updateDeptManagerBadRequest() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        Department department2 = new Department();
        department2.setDeptNo("ZX99");
        department2.setDeptName("Test"+2);
        departmentRepository.save(department2);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateDeptManagerRequest requestDeptNoTooLong = new UpdateDeptManagerRequest();
        requestDeptNoTooLong.setDeptNo("Dept No too long. Dept No too long. Dept No too long. Dept No too long. Dept No too long. Dept No too long.Dept No too long. Dept No too long. ");
        requestDeptNoTooLong.setFromDate("2019-04-24");
        requestDeptNoTooLong.setToDate("2023-01-01");


        //
        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/managers/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNoTooLong))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);
            assertNotEquals(DeptManagerTest.getDepartment().getDeptNo(),requestDeptNoTooLong.getDeptNo());
            assertNotEquals(DeptManagerTest.getFromDate(),requestDeptNoTooLong.getFromDate());
            assertNotEquals(DeptManagerTest.getToDate(),requestDeptNoTooLong.getToDate());
        });

    }

    @Test
    void updateDeptManagerNotFound() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        Department department2 = new Department();
        department2.setDeptNo("ZX99");
        department2.setDeptName("Test"+2);
        departmentRepository.save(department2);

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

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateDeptManagerRequest request = new UpdateDeptManagerRequest();
        request.setDeptNo(department2.getDeptNo());
        request.setFromDate("2019-04-24");
        request.setToDate("2023-01-01");


        //
        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/managers/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);
            assertNotEquals(DeptManagerTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(DeptManagerTest.getFromDate(),request.getFromDate());
            assertNotEquals(DeptManagerTest.getToDate(),request.getToDate());
        });

        mockMvc.perform(
                put("/api/departments/AT14/managers/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);
            assertNotEquals(DeptManagerTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(DeptManagerTest.getFromDate(),request.getFromDate());
            assertNotEquals(DeptManagerTest.getToDate(),request.getToDate());
        });

        mockMvc.perform(
                put("/api/departments/"+department2.getDeptNo()+"/managers/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);
            assertNotEquals(DeptManagerTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(DeptManagerTest.getFromDate(),request.getFromDate());
            assertNotEquals(DeptManagerTest.getToDate(),request.getToDate());
        });

        mockMvc.perform(
                put("/api/departments/AT14/managers/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);
            assertNotEquals(DeptManagerTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(DeptManagerTest.getFromDate(),request.getFromDate());
            assertNotEquals(DeptManagerTest.getToDate(),request.getToDate());
        });

        UpdateDeptManagerRequest requestNewDeptNotFound = new UpdateDeptManagerRequest();
        requestNewDeptNotFound.setDeptNo("PR99");
        requestNewDeptNotFound.setFromDate("2019-04-24");
        requestNewDeptNotFound.setToDate("2023-01-01");

        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/managers/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestNewDeptNotFound))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptManagerResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);
            assertNotEquals(DeptManagerTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(DeptManagerTest.getFromDate(),request.getFromDate());
            assertNotEquals(DeptManagerTest.getToDate(),request.getToDate());
        });

    }

    @Test
    void deleteDeptManagerSuccess() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        mockMvc.perform(
                delete("/api/departments/"+department.getDeptNo()+"/managers/"+employee.getEmpNo())
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
            assertNotNull(response.getLinks());

            assertEquals("OK",response.getData());

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNull(DeptManagerTest);

        });

    }

    @Test
    void deleteDeptManagerNotFound() throws Exception{

        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        Department department2 = new Department();
        department2.setDeptNo("X22");
        department2.setDeptName("Test"+2);
        departmentRepository.save(department);

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

        RegisterDeptManagerRequest requestInsert = new RegisterDeptManagerRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/managers")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        mockMvc.perform(
                delete("/api/departments/"+department.getDeptNo()+"/managers/0")
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

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);

        });

        mockMvc.perform(
                delete("/api/departments/X55/managers/"+employee.getEmpNo())
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

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);

        });

        mockMvc.perform(
                delete("/api/departments/"+department2.getDeptNo()+"/managers/"+employee2.getEmpNo())
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

            DeptManager DeptManagerTest = DeptManagerRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(DeptManagerTest);

        });
    }




}
