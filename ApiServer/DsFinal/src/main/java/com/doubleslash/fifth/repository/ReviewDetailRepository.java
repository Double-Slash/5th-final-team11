package com.doubleslash.fifth.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doubleslash.fifth.entity.review.ReviewDetail;


@Repository
public interface ReviewDetailRepository extends JpaRepository<ReviewDetail, Long> {
	
	// 리뷰 리스트 조회
	@Query(value = "select r from ReviewDetail r join fetch r.user u join fetch r.alcohol a where r.alcohol.id=?1 order by field(u.id, ?2) desc")
	public List<ReviewDetail> findByAid(Long aid, Long id, Pageable pageable);

	// 내 기록 조회
	@Query(value = "select r from ReviewDetail r where r.user.id = ?1")
	public Page<ReviewDetail> findByUserIdOrderBy(Long id, Pageable pageable);
}

