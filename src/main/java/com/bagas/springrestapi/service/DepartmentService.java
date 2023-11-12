package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Department;
import com.bagas.springrestapi.model.DepartmentResponse;
import com.bagas.springrestapi.model.EmployeeResponse;
import com.bagas.springrestapi.model.RegisterDepartmentRequest;
import com.bagas.springrestapi.model.UpdateDepartmentRequest;
import com.bagas.springrestapi.repository.DepartmentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ValidationService validationService;


    @Transactional
    public void registerDepartment(RegisterDepartmentRequest request){
        validationService.validate(request);

        Optional<Department> dept = departmentRepository.findById(request.getDeptNo());

        if (dept.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department No is already registered");
        }
        Department department = new Department();
        department.setDeptNo(request.getDeptNo());
        department.setDeptName(request.getDeptName());
        departmentRepository.save(department);
    }

    @Transactional
    public DepartmentResponse updateDepartment(String deptNo,UpdateDepartmentRequest request){
        validationService.validate(request);

        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        if (Objects.nonNull(request.getDeptName())){
            department.setDeptName(request.getDeptName());
            departmentRepository.save(department);
        }

        return toDepartmentResponse(department);


    }

    private DepartmentResponse toDepartmentResponse(Department department){
        return DepartmentResponse.builder()
                .deptNo(department.getDeptNo())
                .deptName(department.getDeptName()).build();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentByDeptNo(String deptNo){

        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        return toDepartmentResponse(department);


    }

    @Transactional
    public void deleteDepartment(String deptNo){

        Department department = departmentRepository.findById(deptNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Department is not found"));

        departmentRepository.delete(department);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> searchDepartment(String keyword, Integer page, Integer size){
        Specification<Department> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(keyword)){
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("deptNo")),"%"+keyword.toLowerCase()+"%"
                        ),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("deptName")),"%"+keyword.toLowerCase()+"%"
                        )
                ));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };

        Pageable pageable = PageRequest.of(page,size, Sort.by("deptNo").ascending());
        Page<Department> departments = departmentRepository.findAll(specification,pageable);
        List<DepartmentResponse> departmentResponseList = departments.stream()
                .map(this::toDepartmentResponse).toList();

        return new PageImpl<>(departmentResponseList,pageable,departments.getTotalElements());

    }
}
