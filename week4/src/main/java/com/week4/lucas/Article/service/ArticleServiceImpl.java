package com.week4.lucas.Article.service;


import com.week4.lucas.Article.mapper.ArticleMapper;
import com.week4.lucas.Article.Likes.entity.Likes;
import com.week4.lucas.Article.Likes.repository.LikesRepository;
import com.week4.lucas.Article.dto.request.ArticleReq;
import com.week4.lucas.Article.entity.Article;
import com.week4.lucas.Article.repository.ArticleRepository;
import com.week4.lucas.Comment.repository.CommentRepository;
import com.week4.lucas.User.entity.User;
import com.week4.lucas.User.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final EntityManager em;
    private final ArticleRepository articleRepo;
    private final LikesRepository likesRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;

    @Override
    public List<Article> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "articleCreatedAt"));
        return articleRepo.findAllByIsDeletedFalse(pageable).getContent();
    }

    @Transactional
    @Override
    public Article create(ArticleReq.CreateArticleReq req) {
        User user = userRepo.findById(req.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return articleRepo.save(ArticleMapper.toEntity(user,req));
    }
    @Transactional
    @Override
    public Article detail(Long articleId, boolean increaseViews) {
        Article a = articleRepo.findById(articleId).orElse(null);
        if (a == null) return null;
        if (increaseViews) a.increaseView();
        a.setLikeCount(likesRepo.countByArticleId(articleId));
        a.setCommentCount(commentRepo.countByArticleId(articleId));
        return a;
    }

    @Transactional
    @Override
    public Article edit(Long articleId, ArticleReq.EditArticleReq req) throws ForbiddenException {
        Article a = articleRepo.findById(articleId).orElse(null);
        if (a == null) return null;
        //본인 확인
        if (!Objects.equals(a.getUser().getId(), req.userId())) throw new ForbiddenException();
        if (req.title() != null) a.setTitle(req.title());
        if (req.content() != null) a.setContent(req.content());
        return a;
    }

    @Override
    public boolean delete(Long articleId) {

        return articleRepo.findById(articleId).map(a->{
            a.softDelete();
            return true;
        }).orElse(false);
    }
    @Transactional
    @Override
    public boolean like(Long articleId,Long userId) {
        if (likesRepo.existsByArticleIdAndUserId(articleId, userId)) {
            return false;
        }
        // 없으면 저장 후 true
        Article aRef = em.getReference(Article.class, articleId);
        User uRef    = em.getReference(User.class, userId);
        likesRepo.save(Likes.builder().article(aRef).user(uRef).build());
        return true;
    }
    @Transactional
    @Override
    public boolean unlike(Long articleId,Long userId) {
        if (!likesRepo.existsByArticleIdAndUserId(articleId, userId)) {
            return false;
        }
        // 있으면 삭제 후 true
        likesRepo.deleteByArticleIdAndUserId(articleId, userId);
        return true;
    }
}