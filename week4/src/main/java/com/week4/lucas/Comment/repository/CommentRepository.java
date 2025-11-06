package com.week4.lucas.Comment.repository;

import com.week4.lucas.Comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Integer countByArticleId(Long articleId);
}
