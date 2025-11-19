package com.week4.lucas.Article.service;


import com.week4.lucas.Article.dto.response.ArticleDetailRes;
import com.week4.lucas.Article.dto.response.ArticleSummaryRes;
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

    @Transactional
    @Override
    public List<Article> list(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "articleCreatedAt"));
        return articleRepo.findAllByIsDeletedFalse(pageable).getContent();
    }

    @Transactional
    @Override
    public ArticleDetailRes create(Long userId, ArticleReq.CreateArticleReq req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Article article = articleRepo.save(ArticleMapper.toEntity(user, req));
        return ArticleMapper.toArticleDetail(article,false,true);
    }
    @Transactional
    @Override
    public ArticleDetailRes detail(Long userId,Long articleId, boolean increaseViews) {
        Article article = articleRepo.findById(articleId).orElse(null);
        if (article == null) return null;
        if (increaseViews) article.increaseView();
        article.setLikeCount(likesRepo.countByArticleId(articleId));
        article.setCommentCount(commentRepo.countByArticleId(articleId));
        boolean likedByMe = userId != null && likesRepo.existsByArticleIdAndUserId(articleId,userId); //내가 좋아요를 눌렀는지
        boolean isAuthor = userId != null && articleRepo.existsByIdAndUserId(articleId,userId); //내가 게시글을 작성했는지
        return ArticleMapper.toArticleDetail(article,likedByMe,isAuthor); //DTO 반환
    }

    @Transactional
    @Override
    public ArticleDetailRes edit(Long articleId, Long userId, ArticleReq.EditArticleReq req) throws ForbiddenException {
        Article article = articleRepo.findById(articleId).orElse(null);
        if (article == null) return null;
        //본인이 작성한 글이 맞는지 확인
        if (!Objects.equals(article.getUser().getId(), userId)) throw new ForbiddenException();
        if (req.title() != null) article.setTitle(req.title());
        if (req.content() != null) article.setContent(req.content());
        boolean likedByMe = likesRepo.existsByArticleIdAndUserId(articleId,userId); //내가 좋아요를 눌렀는지
        boolean isAuthor = userId != null && articleRepo.existsByIdAndUserId(articleId,userId); //내가 게시글을 작성했는지
        return ArticleMapper.toArticleDetail(article,likedByMe,isAuthor) ;
    }
    
    @Transactional
    @Override
    public boolean delete(Long articleId,Long userId) {
        Article aRef = em.getReference(Article.class, articleId);
        if (aRef == null) return false;
        if (!Objects.equals(aRef.getUser().getId(), userId)) throw new ForbiddenException();
        aRef.softDelete();
        return true;
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
        aRef.setLikeCount(likesRepo.countByArticleId(articleId));
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
        Article aRef = em.getReference(Article.class, articleId);
        aRef.setLikeCount(likesRepo.countByArticleId(articleId));
        return true;
    }
}
