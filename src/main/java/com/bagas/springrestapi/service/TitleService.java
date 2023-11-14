package com.bagas.springrestapi.service;

import com.bagas.springrestapi.entity.Employee;
import com.bagas.springrestapi.entity.Title;
import com.bagas.springrestapi.model.RegisterTitleRequest;
import com.bagas.springrestapi.model.TitleResponse;
import com.bagas.springrestapi.model.UpdateTitleRequest;
import com.bagas.springrestapi.repository.EmployeeRepository;
import com.bagas.springrestapi.repository.TitleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TitleService {

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public void registerTitle(RegisterTitleRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = employeeRepository.employeeByEmpNo(request.getEmpNo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Optional<Title> title = titleRepository.findById(request.getEmpNo());

        if (title.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Employee already have Title");
        }

        titleRepository.insertIntoTitle(employee.getEmpNo(),sdf.parse(request.getFromDate()),request.getTitle(),sdf.parse(request.getToDate()));
    }

    @Transactional
    public TitleResponse updateTitle(Integer empNo, UpdateTitleRequest request) throws ParseException {
        validationService.validate(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Employee employee = employeeRepository.employeeByEmpNo(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee is not found"));

        Title title = titleRepository.titleByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Title is not found"));

        String newTitle = request.getTitle();
        Date newFromDate = sdf.parse(request.getFromDate());
        Date newToDate = sdf.parse(request.getToDate());

        if (Objects.isNull(request.getTitle())){
            newTitle = title.getTitle();
        }

        if (Objects.isNull(request.getFromDate())){
            newFromDate = title.getFromDate();
        }

        if (Objects.isNull(request.getToDate())){
            newToDate = title.getToDate();
        }

        titleRepository.updateTitle(empNo,newFromDate,newTitle,newToDate);

        Title updatedTitle = new Title();
        updatedTitle.setEmpNo(empNo);
        updatedTitle.setTitle(newTitle);
        updatedTitle.setFromDate(newFromDate);
        updatedTitle.setToDate(newToDate);

        return toTitleResponse(updatedTitle);
    }

    private TitleResponse toTitleResponse(Title title){
        return TitleResponse.builder()
                .empNo(title.getEmpNo())
                .title(title.getTitle())
                .fromDate(title.getFromDate())
                .toDate(title.getToDate())
                .build();
    }

    @Transactional(readOnly = true)
    public TitleResponse getTitleByEmpNo(Integer empNo){
        Title title = titleRepository.titleByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Title is not found"));

        return toTitleResponse(title);
    }

    @Transactional
    public void deleteTitle(Integer empNo){
        Employee employee = employeeRepository.employeeByEmpNo(empNo)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee is not found"));

        Title title = titleRepository.titleByEmpNo(empNo)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Employee Title is not found"));

        titleRepository.deleteTitle(empNo);
    }

    @Transactional(readOnly = true)
    public Page<TitleResponse> getAllTitle(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("emp_no").ascending());

        Page<Title> titles = titleRepository.allSalaryWithPageable(pageable);
        List<TitleResponse> titleResponseList = titles.getContent().stream()
                .map(this::toTitleResponse).toList();
        return new PageImpl<>(titleResponseList,pageable,titles.getTotalElements());

    }

    @Transactional(readOnly = true)
    public Page<TitleResponse> searchTitle (String keyword, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page,size,Sort.by("emp_no").ascending());
        Page<Title> titles;
        if (Objects.nonNull(keyword)){
            titles = titleRepository.searchTitle(keyword,pageable);
        }else {
            titles = titleRepository.allSalaryWithPageable(pageable);
        }

        List<TitleResponse> titleResponseList = titles.getContent().stream()
                .map(this::toTitleResponse).toList();
        return new PageImpl<>(titleResponseList,pageable,titles.getTotalElements());
    }

}
