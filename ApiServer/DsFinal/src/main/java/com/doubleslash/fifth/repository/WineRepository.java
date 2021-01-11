package com.doubleslash.fifth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doubleslash.fifth.vo.WineVO;

@Repository
public interface WineRepository extends JpaRepository<WineVO, Integer>{
	
}
