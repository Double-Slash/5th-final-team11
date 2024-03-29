package com.doubleslash.fifth.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doubleslash.fifth.entity.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer>{

	@Modifying
	@Transactional
	@Query(value = "insert into Rating(id, aid, star) values(?1, ?2, ?3)", nativeQuery = true)
	public void insert(Long id, Long aid, float star);
	
}
