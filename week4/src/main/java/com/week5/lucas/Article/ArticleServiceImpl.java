package com.week5.lucas.Article;


import com.week5.lucas.Article.dto.request.ArticleDto;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository repo;

    public ArticleServiceImpl(ArticleRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Article> list(int page, int size) {
        return repo.findPage(page, size);
    }

    @Override
    public Article create(ArticleDto.CreateArticleReq req) {
        return repo.saveArticle(ArticleMapper.toEntity(req));
    }

    @Override
    public Article detail(Long articleId, boolean increaseViews) {
        Article a = repo.findArticle(articleId);
        if (a != null && increaseViews) repo.increaseViews(articleId);
        return a;
    }

    @Override
    public Article edit(Long articleId, ArticleDto.EditArticleReq req) throws ForbiddenException {
        Article a = repo.findArticle(articleId);
        if (a == null) return null;
        if (!Objects.equals(a.getUserId(), req.userId())) throw new ForbiddenException();

        if (StringUtils.hasText(req.title())) a.setTitle(req.title());
        if (StringUtils.hasText(req.content())) a.setContent(req.content());
        if (StringUtils.hasText(req.fileName())) a.setFileName(req.fileName());
        return a;
    }

    @Override
    public boolean delete(Long articleId) {
        return repo.deleteArticle(articleId);
    }

    @Override
    public Comment createComment(Long articleId, ArticleDto.CreateCommentReq req) {
        Article a = repo.findArticle(articleId);
        if (a == null) return null;
        return repo.saveComment(ArticleMapper.toEntity(articleId, req));
    }

    @Override
    public Comment editComment(Long articleId, Long commentId, ArticleDto.EditCommentReq req) {
        Comment c = repo.findComment(commentId);
        if (c == null || !Objects.equals(c.getArticleId(), articleId)) return null;
        c.setContent(req.content());
        return c;
    }

    @Override
    public boolean deleteComment(Long articleId, Long commentId) {
        Comment c = repo.findComment(commentId);
        if (c == null || !Objects.equals(c.getArticleId(), articleId)) return false;
        return repo.deleteComment(commentId);
    }

    @Override
    public Article like(Long articleId) {
        return repo.addLike(articleId, 1);
    }

    @Override
    public Article unlike(Long articleId) {
        return repo.addLike(articleId, -1);
    }
}