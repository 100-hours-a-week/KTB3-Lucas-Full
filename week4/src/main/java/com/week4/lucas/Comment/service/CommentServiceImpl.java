package com.week4.lucas.Comment.service;

import com.week4.lucas.Article.entity.Article;
import com.week4.lucas.Comment.mapper.CommentMapper;
import com.week4.lucas.Comment.dto.request.CommentReq;
import com.week4.lucas.Comment.dto.response.CommentRes;
import com.week4.lucas.Comment.entity.Comment;
import com.week4.lucas.Comment.repository.CommentRepository;
import com.week4.lucas.User.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final EntityManager em;
    private final CommentRepository commentRepo;
    @Override
    public CommentRes createComment(Long articleId, CommentReq.CreateCommentReq req) {
        Article articleRef = em.getReference(Article.class, articleId);
        User userRef = em.getReference(User.class, req.userId());

        Comment c = commentRepo.save(CommentMapper.toEntity(articleRef, userRef, req));
        return CommentMapper.toRes(c);
    }

    @Override
    public CommentRes editComment(Long articleId, Long commentId, CommentReq.EditCommentReq req) {
        Comment c = commentRepo.findById(commentId).orElse(null);
        if (c == null || !c.getArticle().getId().equals(articleId)) return null;
        if (!c.getUser().getId().equals(req.userId())) return null; // 권한 체크(간단버전)

        c.edit(req.content()); // 더티체킹으로 UPDATE
        return CommentMapper.toRes(c);
    }

    @Override
    public boolean deleteComment(Long articleId, Long commentId) {
        Comment c = commentRepo.findById(commentId).orElse(null);
        if (c == null || !c.getArticle().getId().equals(articleId)) return false;

        c.softDelete(); // 소프트 삭제
        return true;
    }
}
