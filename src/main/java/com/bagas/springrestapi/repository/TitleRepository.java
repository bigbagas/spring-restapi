package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {

    @Modifying
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

    @Modifying
    @Query(
            value = "select delete_title(:empNo)",
            nativeQuery = true
    )
    void deleteTitle(Integer empNo);


}
