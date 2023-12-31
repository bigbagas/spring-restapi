package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Title;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {

    @Query(
            value = "select insert_title(:empNo, :fromDate, :title,:toDate)",
            nativeQuery = true
    )
    void insertIntoTitle(Integer empNo, Date fromDate, String title, Date toDate);

    @Query(
            value = "select * from titles t where t.emp_no = :empNo",
            nativeQuery = true
    )
    Optional<Title> titleByEmpNo(Integer empNo);

    @Query(
            value = "select update_title(:empNo, :fromDate, :title,:toDate)",
            nativeQuery = false
    )
    void updateTitle(Integer empNo, Date fromDate, String title, Date toDate);

    @Query(
            value = "select delete_title(:empNo)",
            nativeQuery = true
    )
    void deleteTitle(Integer empNo);

    @Query(
            value = "select * from titles order by titles.emp_no",
            countQuery = "select count(*) from titles",
            nativeQuery = true
    )
    Page<Title> allSalaryWithPageable(Pageable pageable);

    @Query(
            value = "select * from titles where title like %:title% order by titles.emp_no",
            countQuery = "select count(*) from titles",
            nativeQuery = true
    )
    Page<Title> searchTitle(String title, Pageable pageable);




}
