package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {

    @Query(
            value = "select insert_title(:empNo, :fromDate, :title,:toDate)",
            nativeQuery = true
    )
    void insertIntoTitle(Integer empNo, Date fromDate, String title, Date toDate);


}
