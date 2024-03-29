package com.doubleslash.fifth.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.doubleslash.fifth.dto.AlcoholSearchDTO;
import com.doubleslash.fifth.entity.ViewSearch;
import com.doubleslash.fifth.mapping.SearchMapping;

@Repository
public interface SearchRepository extends JpaRepository<ViewSearch, Long>{
	
	//전체 주류 조회
	public Page<SearchMapping> findByAidIsNotNull(Pageable pageable);
	
	//카테고리별 주류 조회
	public Page<SearchMapping> findByCategory(String category, Pageable pageable);
	
	//주류명으로 검색해서 조회
	public Page<SearchMapping> findByNameContaining(String name, Pageable pageable);
	
	@Query(value = "select new com.doubleslash.fifth.dto.AlcoholSearchDTO(v.aid, v.name, v.category, v.thumbnail, v.star, v.reviewCnt) from ViewSearch as v, Recommend as r where v.aid = r.aid and r.id = ?1 and processed = 'Y' order by recScore desc")
	public Page<AlcoholSearchDTO> getRecommendDefaultAll(Long id, Pageable pageable); 
	
	@Query(value = "select new com.doubleslash.fifth.dto.AlcoholSearchDTO(v.aid, v.name, v.category, v.thumbnail, v.star, v.reviewCnt) from ViewSearch as v, Recommend as r where v.aid = r.aid and r.id = ?1 and category = ?2 and processed = 'N' order by recScore desc")
	public Page<AlcoholSearchDTO> getRecommendDefault(Long id, String category, Pageable pageable); 
	
	@Query(value = "select new com.doubleslash.fifth.dto.AlcoholSearchDTO(v.aid, v.name, v.category, v.thumbnail, v.star, v.reviewCnt) from ViewSearch as v, Recommend as r where v.aid = r.aid and r.id = ?1 and processed = 'Y'")
	public Page<AlcoholSearchDTO> getRecommendSortingAll(Long id, Pageable pageable); 
	
	@Query(value = "select new com.doubleslash.fifth.dto.AlcoholSearchDTO(v.aid, v.name, v.category, v.thumbnail, v.star, v.reviewCnt) from ViewSearch as v, Recommend as r where v.aid = r.aid and r.id = ?1 and category = ?2 and processed = 'N'")
	public Page<AlcoholSearchDTO> getRecommendSorting(Long id, String category, Pageable pageable); 

	@Query(value = "select new com.doubleslash.fifth.dto.AlcoholSearchDTO(v.aid, v.name, v.category, v.thumbnail, v.star, v.reviewCnt) from ViewSearch as v, Recommend as r where v.aid = r.aid and r.id = ?1 order by rand()")
	public List<AlcoholSearchDTO> getRecommendRand(Long id, Pageable pageable);
	
}