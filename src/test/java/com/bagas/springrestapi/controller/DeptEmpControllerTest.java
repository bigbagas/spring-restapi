package com.bagas.springrestapi.controller;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.entity.DeptEmp;
import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.model.DeptEmpResponse;
import com.bagas.springrestapi.model.RegisterDeptEmpRequest;
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

        RegisterDeptEmpRequest request = new RegisterDeptEmpRequest();
        request.setDeptNo(department.getDeptNo());
        request.setEmpNo(employee.getEmpNo());
        request.setFromDate(sdf.parse("2020-09-21"));
        request.setToDate(sdf.parse("2023-09-21"));

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
            assertEquals(request.getFromDate(),deptEmpTest.getFromDate());
            assertEquals(request.getToDate(),deptEmpTest.getToDate());

        });
    }

    @Test
    void registerDeptEmpBadRequest() throws Exception{
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

        RegisterDeptEmpRequest requestDeptNoTooLong = new RegisterDeptEmpRequest();
        requestDeptNoTooLong.setDeptNo("Dept No is too long. Dept No is too long.");
        requestDeptNoTooLong.setEmpNo(1);
        requestDeptNoTooLong.setFromDate(sdf.parse("2020-09-21"));
        requestDeptNoTooLong.setToDate(sdf.parse("2023-09-21"));

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

        DeptEmp deptEmp = new DeptEmp();
        deptEmp.setEmpNo(employee.getEmpNo());
        deptEmp.setDepartment(department);
        deptEmp.setFromDate(sdf.parse("2021-01-11"));
        deptEmp.setToDate(sdf.parse("2022-12-12"));
        deptEmpRepository.save(deptEmp);

        RegisterDeptEmpRequest requestDeptNoIsNotFound = new RegisterDeptEmpRequest();
        requestDeptNoIsNotFound.setDeptNo("Dept");
        requestDeptNoIsNotFound.setEmpNo(1);
        requestDeptNoIsNotFound.setFromDate(sdf.parse("2020-09-21"));
        requestDeptNoIsNotFound.setToDate(sdf.parse("2023-09-21"));

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

        RegisterDeptEmpRequest requestEmpNoIsNotFound = new RegisterDeptEmpRequest();
        requestDeptNoIsNotFound.setDeptNo("Test");
        requestDeptNoIsNotFound.setEmpNo(33);
        requestDeptNoIsNotFound.setFromDate(sdf.parse("2020-09-21"));
        requestDeptNoIsNotFound.setToDate(sdf.parse("2023-09-21"));

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
        requestInsert.setFromDate(sdf.parse("2020-09-21"));
        requestInsert.setToDate(sdf.parse("2023-09-21"));

        mockMvc.perform(
                post("/api/departments/employees")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestInsert))
        );

        RegisterDeptEmpRequest requestAlreadyRegisterInThisDept = new RegisterDeptEmpRequest();
        requestAlreadyRegisterInThisDept.setDeptNo(department.getDeptNo());
        requestAlreadyRegisterInThisDept.setEmpNo(employee.getEmpNo());
        requestAlreadyRegisterInThisDept.setFromDate(sdf.parse("2020-09-21"));
        requestAlreadyRegisterInThisDept.setToDate(sdf.parse("2023-09-21"));

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

        RegisterDeptEmpRequest requestAlreadyRegisterInOtherDept = new RegisterDeptEmpRequest();
        requestAlreadyRegisterInOtherDept.setDeptNo(department2.getDeptNo());
        requestAlreadyRegisterInOtherDept.setEmpNo(employee.getEmpNo());
        requestAlreadyRegisterInOtherDept.setFromDate(sdf.parse("2020-09-21"));
        requestAlreadyRegisterInOtherDept.setToDate(sdf.parse("2023-09-21"));

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

            RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate(sdf.parse("2020-09-21"));
            requestInsert.setToDate(sdf.parse("2023-09-21"));

            mockMvc.perform(
                    post("/api/departments/employees")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );


        }


        mockMvc.perform(
                get("/api/departments/B16/employees")
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
    void getDeptEmpByDeptNoIsNotFound() throws Exception{

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

            RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate(sdf.parse("2020-09-21"));
            requestInsert.setToDate(sdf.parse("2023-09-21"));

            mockMvc.perform(
                    post("/api/departments/employees")
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(requestInsert))
            );


        }


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

            RegisterDeptEmpRequest requestInsert = new RegisterDeptEmpRequest();
            requestInsert.setDeptNo(department.getDeptNo());
            requestInsert.setEmpNo(j+1);
            requestInsert.setFromDate(sdf.parse("2020-09-21"));
            requestInsert.setToDate(sdf.parse("2023-09-21"));

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
        requestInsert.setFromDate(sdf.parse("2020-09-21"));
        requestInsert.setToDate(sdf.parse("2023-09-21"));

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
        requestInsert.setFromDate(sdf.parse("2020-09-21"));
        requestInsert.setToDate(sdf.parse("2023-09-21"));

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


}
