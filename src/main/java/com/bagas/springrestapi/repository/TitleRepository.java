package com.bagas.springrestapi.repository;

import com.bagas.springrestapi.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TitleRepository extends JpaRepository<Title, Integer> {
}
