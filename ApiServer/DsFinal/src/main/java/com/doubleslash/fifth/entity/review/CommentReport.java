package com.doubleslash.fifth.entity.review;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.doubleslash.fifth.entity.BaseEntity;
import com.doubleslash.fifth.entity.User;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class CommentReport extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "commentReport_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id")
	private Comment comment;
	
	private String content;
	
    public void addReportComment(User user, Comment comment, String content) {
        this.user = user;
        this.comment = comment;
        this.content = content;
        comment.getCommentReports().add(this);
    }
}
