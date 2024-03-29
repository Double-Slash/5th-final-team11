package com.doubleslash.fifth.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {

	@CreatedDate
	@Column(name = "create_time", updatable = false)
	private LocalDateTime createdDate;
	
	@LastModifiedDate
	@Column(name = "update_time")
	private LocalDateTime updatedDate;

}
