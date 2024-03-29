package com.doubleslash.fifth.entity.review;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.doubleslash.fifth.entity.BaseEntity;
import com.doubleslash.fifth.entity.User;

import lombok.Data;
import lombok.Getter;

@Entity
@Getter
public class ReviewLove extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reviewLove_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id")
	private Review review;
	
	public void addLoveReview(User user, Review review) {
		this.user = user;
		this.review = review;
		review.getReviewLoves().add(this);
	}
	
}
