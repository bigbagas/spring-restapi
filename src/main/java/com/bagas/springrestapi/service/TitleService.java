package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Title;
import com.bagas.springrestapi.model.RegisterTitleRequest;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.bagas.springrestapi.repository.TitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class TitleService {

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationService validationService;

    public void registerTitle(RegisterTitleRequest request){
        validationService.validate(request);

        Employee employee = employeeRepository.findById(request.getEmpNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Optional<Title> title = titleRepository.findById(request.getEmpNo());

        if (title.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee already have Title");
        }

        titleRepository.insertIntoTitle(employee.getEmpNo(),request.getFromDate(),request.getTitle(),request.getToDate());




    }
}
