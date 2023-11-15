package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptEmp;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.DeptEmpResponse;
import com.bagas.springrestapi.model.RegisterDeptEmpRequest;
import com.bagas.springrestapi.model.UpdateDeptEmpRequest;
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
class DeptEmpControllerTest {

    @Autowired
    private DeptEmpRepository deptEmpRepository;

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
        deptEmpRepository.deleteAll();
        deptManagerRepository.deleteAll();
        departmentRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void registerDeptEmpSuccess() throws Exception{
        // insert Department first
        Department department = new Department();
        department.setDeptNo("Test");
        department.setDeptName("Test");
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // insert employee first
        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        RegisterDeptEmpRequest request = new RegisterDeptEmpRequest();
        request.setDeptNo(department.getDeptNo());
        request.setEmpNo(employee.getEmpNo());
        request.setFromDate("2020-09-21");
        request.setToDate("2023-09-21");

        // register success
        mockMvc.perform(
                post("/api/departments/employees")
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(request.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertEquals(request.getDeptNo(),deptEmpTest.getDepartment().getDeptNo());
            assertEquals(request.getEmpNo(),deptEmpTest.getEmpNo());
            assertEquals(sdf.parse(request.getFromDate()),deptEmpTest.getFromDate());
            assertEquals(sdf.parse(request.getToDate()),deptEmpTest.getToDate());

        });
    }

    @Test
    void registerDeptEmpBadRequest() throws Exception{
        // insert department
        Department department = new Department();
        department.setDeptNo("Test");
        department.setDeptName("Test");
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // insert department
        Employee employee = new Employee();
        employee.setBirthDate(sdf.parse("1995-08-22"));
        employee.setFirstName("Test");
        employee.setLastName("Test");
        employee.setGender("M");
        employee.setHireDate(sdf.parse("2020-09-21"));
        employeeRepository.save(employee);

        // dept no is too long (more than 4)
        RegisterDeptEmpRequest requestDeptNoTooLong = new RegisterDeptEmpRequest();
        requestDeptNoTooLong.setDeptNo("Dept No is too long. Dept No is too long.");
        requestDeptNoTooLong.setEmpNo(1);
        requestDeptNoTooLong.setFromDate("2020-09-21");
        requestDeptNoTooLong.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(requestDeptNoTooLong.getEmpNo()).orElse(null);
            assertNull(deptEmpTest);

        });

        // to date is null
        RegisterDeptEmpRequest requestToDateIsNull = new RegisterDeptEmpRequest();
        requestToDateIsNull.setDeptNo("Dep1");
        requestToDateIsNull.setEmpNo(1);
        requestToDateIsNull.setFromDate("2020-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestToDateIsNull))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());

            DeptEmp deptEmpTest = deptEmpRepository.findById(requestToDateIsNull.getEmpNo()).orElse(null);
            assertNull(deptEmpTest);

        });
    }

    @Test
    void registerDeptEmpNotFound() throws Exception{
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

        // dept no is not found in Departments
        RegisterDeptEmpRequest requestDeptNoIsNotFound = new RegisterDeptEmpRequest();
        requestDeptNoIsNotFound.setDeptNo("Dept");
        requestDeptNoIsNotFound.setEmpNo(1);
        requestDeptNoIsNotFound.setFromDate("2020-09-21");
        requestDeptNoIsNotFound.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
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
            DeptEmp deptEmpTest = deptEmpRepository.findById(requestDeptNoIsNotFound.getEmpNo()).orElse(null);
            assertNull(deptEmpTest);
        });

        // emp no is not found in employee
        RegisterDeptEmpRequest requestEmpNoIsNotFound = new RegisterDeptEmpRequest();
        requestEmpNoIsNotFound.setDeptNo(department.getDeptNo());
        requestEmpNoIsNotFound.setEmpNo(33);
        requestEmpNoIsNotFound.setFromDate("2020-09-21");
        requestEmpNoIsNotFound.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(requestDeptNoIsNotFound.getEmpNo()).orElse(null);
            assertNull(deptEmpTest);

        });
    }

    @Test
    void registerDeptEmpAlreadyRegister() throws Exception{
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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        // insert to dept emp
        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        RegisterDeptEmpRequest requestAlreadyRegisterInThisDept = new RegisterDeptEmpRequest();
        requestAlreadyRegisterInThisDept.setDeptNo(department.getDeptNo());
        requestAlreadyRegisterInThisDept.setEmpNo(employee.getEmpNo());
        requestAlreadyRegisterInThisDept.setFromDate("2020-09-21");
        requestAlreadyRegisterInThisDept.setToDate("2023-09-21");

        // data already in dept emp(duplicate)
        mockMvc.perform(
                post("/api/departments/employees")
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


        // data already in dept emp(register in other dept)
        RegisterDeptEmpRequest requestAlreadyRegisterInOtherDept = new RegisterDeptEmpRequest();
        requestAlreadyRegisterInOtherDept.setDeptNo(department2.getDeptNo());
        requestAlreadyRegisterInOtherDept.setEmpNo(employee.getEmpNo());
        requestAlreadyRegisterInOtherDept.setFromDate("2020-09-21");
        requestAlreadyRegisterInOtherDept.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
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
    void getDeptEmpByDeptNoIsSuccess() throws Exception{

        //insert dept
            Department department = new Department();
            department.setDeptNo("B16");
            department.setDeptName("Test"+1);
            departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // looping insert employee
        for (int i = 0; i < 20; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);
        }


        //looping insert dept emp
        for (int j = 0; j < 20; j++) {
            RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate("2020-09-21");
            requestInsert.setToDate("2023-09-21");

            mockMvc.perform(
                    post("/api/departments/employees")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );
        }

        // success
        mockMvc.perform(
                get("/api/departments/B16/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<DeptEmpResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            for (int i = 0; i < response.getData().size(); i++) {
                
            }
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(2,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(20,response.getPaging().getSize());
        });
    }

    @Test
    void getDeptEmpByDeptNoIsNotFound() throws Exception{

        // insert department
        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // insert employee
        for (int i = 0; i < 20; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }

        // insert dept emp
        for (int j = 0; j < 20; j++) {
            RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate("2020-09-21");
            requestInsert.setToDate("2023-09-21");

            mockMvc.perform(
                    post("/api/departments/employees")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );
        }

        // not found
        mockMvc.perform(
                get("/api/departments/Z90/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<List<DeptEmpResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

        });
    }

    @Test
    void getAllDeptEmpByDeptNoIsSuccess() throws Exception{

        // insert department
        Department department = new Department();
        department.setDeptNo("B16");
        department.setDeptName("Test"+1);
        departmentRepository.save(department);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 20; i++) {
            Employee employee = new Employee();
            employee.setBirthDate(sdf.parse("1995-08-22"));
            employee.setFirstName("Test"+i);
            employee.setLastName("Test"+i);
            employee.setGender("M");
            employee.setHireDate(sdf.parse("2020-09-21"));
            employeeRepository.save(employee);

        }


        for (int j = 0; j < 20; j++) {
            RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate("2020-09-21");
            requestInsert.setToDate("2023-09-21");

            mockMvc.perform(
                    post("/api/departments/employees")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );
        }


        mockMvc.perform(
                get("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<DeptEmpResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNotNull(response.getPaging());
            assertEquals(10,response.getData().size());
            assertEquals(2,response.getPaging().getTotalPage());
            assertEquals(0,response.getPaging().getCurrentPage());
            assertEquals(20,response.getPaging().getSize());


        });


    }

    @Test
    void getDeptEmpByDeptNoAndEmpNoIsSuccess() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        mockMvc.perform(
                get("/api/departments/B16/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());
        });


    }

    @Test
    void getDeptEmpByDeptNoAndEmpNoIsNotFound() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        //
        mockMvc.perform(
                get("/api/departments/B16/employees/5")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

        mockMvc.perform(
                get("/api/departments/B22/employees/5")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

        mockMvc.perform(
                get("/api/departments/"+department2.getDeptNo()+"/employees/5")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });

        mockMvc.perform(
                get("/api/departments/"+department2.getDeptNo()+"/employees/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());
        });



    }

    @Test
    void updateDeptEmpSuccess() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateDeptEmpRequest request = new UpdateDeptEmpRequest();
        request.setDeptNo(department2.getDeptNo());
        request.setFromDate("2019-04-24");
        request.setToDate("2023-01-01");


        //
        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
            assertNotNull(response.getData());
            assertNull(response.getErrors());
            assertNull(response.getPaging());
            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertEquals(deptEmpTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertEquals(deptEmpTest.getFromDate(),sdf.parse(request.getFromDate()));
            assertEquals(deptEmpTest.getToDate(),sdf.parse(request.getToDate()));
        });
    }

    @Test
    void updateDeptEmpBadRequest() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateDeptEmpRequest requestDeptNoTooLong = new UpdateDeptEmpRequest();
        requestDeptNoTooLong.setDeptNo("Dept No too long. Dept No too long. Dept No too long. Dept No too long. ");
        requestDeptNoTooLong.setFromDate("2019-04-24");
        requestDeptNoTooLong.setToDate("2023-01-01");


        //
        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestDeptNoTooLong))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertNotEquals(deptEmpTest.getDepartment().getDeptNo(),requestDeptNoTooLong.getDeptNo());
            assertNotEquals(deptEmpTest.getFromDate(),requestDeptNoTooLong.getFromDate());
            assertNotEquals(deptEmpTest.getToDate(),requestDeptNoTooLong.getToDate());
        });

    }

    @Test
    void updateDeptEmpNotFound() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        UpdateDeptEmpRequest request = new UpdateDeptEmpRequest();
        request.setDeptNo(department2.getDeptNo());
        request.setFromDate("2019-04-24");
        request.setToDate("2023-01-01");


        //
        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/employees/0")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertNotEquals(deptEmpTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(deptEmpTest.getFromDate(),request.getFromDate());
            assertNotEquals(deptEmpTest.getToDate(),request.getToDate());
        });

        mockMvc.perform(
                put("/api/departments/AT14/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertNotEquals(deptEmpTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(deptEmpTest.getFromDate(),request.getFromDate());
            assertNotEquals(deptEmpTest.getToDate(),request.getToDate());
        });

        mockMvc.perform(
                put("/api/departments/"+department2.getDeptNo()+"/employees/"+employee2.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertNotEquals(deptEmpTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(deptEmpTest.getFromDate(),request.getFromDate());
            assertNotEquals(deptEmpTest.getToDate(),request.getToDate());
        });

        mockMvc.perform(
                put("/api/departments/AT14/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertNotEquals(deptEmpTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(deptEmpTest.getFromDate(),request.getFromDate());
            assertNotEquals(deptEmpTest.getToDate(),request.getToDate());
        });

        UpdateDeptEmpRequest requestNewDeptNotFound = new UpdateDeptEmpRequest();
        requestNewDeptNotFound.setDeptNo("PR99");
        requestNewDeptNotFound.setFromDate("2019-04-24");
        requestNewDeptNotFound.setToDate("2023-01-01");

        mockMvc.perform(
                put("/api/departments/"+department.getDeptNo()+"/employees/"+employee.getEmpNo())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestNewDeptNotFound))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<DeptEmpResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

            assertNotNull(response.getErrors());
            assertNull(response.getData());
            assertNull(response.getPaging());

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);
            assertNotEquals(deptEmpTest.getDepartment().getDeptNo(),request.getDeptNo());
            assertNotEquals(deptEmpTest.getFromDate(),request.getFromDate());
            assertNotEquals(deptEmpTest.getToDate(),request.getToDate());
        });

    }

    @Test
    void deleteDeptEmpSuccess() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        mockMvc.perform(
                delete("/api/departments/"+department.getDeptNo()+"/employees/"+employee.getEmpNo())
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNull(deptEmpTest);

        });

    }

    @Test
    void deleteDeptEmpNotFound() throws Exception{

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

        RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
        requestInsert.setDeptNo(department.getDeptNo());
        requestInsert.setEmpNo(employee.getEmpNo());
        requestInsert.setFromDate("2020-09-21");
        requestInsert.setToDate("2023-09-21");

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        mockMvc.perform(
                delete("/api/departments/"+department.getDeptNo()+"/employees/0")
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);

        });

        mockMvc.perform(
                delete("/api/departments/X55/employees/"+employee.getEmpNo())
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);

        });

        mockMvc.perform(
                delete("/api/departments/"+department2.getDeptNo()+"/employees/"+employee2.getEmpNo())
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

            DeptEmp deptEmpTest = deptEmpRepository.findById(employee.getEmpNo()).orElse(null);
            assertNotNull(deptEmpTest);

        });
    }




}
