package com.week4.lucas.Article;


import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ArticleRepositoryImpl implements ArticleRepository{

    private final Map<Long, Article> articles = new ConcurrentHashMap<>();
    private final Map<Long, Comment> comments = new ConcurrentHashMap<>();
    private final AtomicLong articleSeq = new AtomicLong(0);
    private final AtomicLong commentSeq = new AtomicLong(0);

    public Article saveArticle(Article a){
        if (a.getId() == null) a.setId(articleSeq.incrementAndGet());
        articles.put(a.getId(), a);
        return a;
    }

    public Article findArticle(Long id){ return articles.get(id); }

    public boolean deleteArticle(Long id){ return articles.remove(id) != null; }

    public List<Article> findPage(int page, int size){
        return articles.values().stream()
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public void increaseViews(Long id){
        Article a = articles.get(id);
        if (a != null) a.setViews(a.getViews() + 1);
    }

    public Article addLike(Long id, int delta){
        Article a = articles.get(id);
        if (a == null) return null;
        a.setLikes(Math.max(0, a.getLikes() + delta));
        return a;
    }

    public Comment saveComment(Comment c){
        if (c.getId() == null) c.setId(commentSeq.incrementAndGet());
        comments.put(c.getId(), c);
        return c;
    }

    public Comment findComment(Long id){ return comments.get(id); }

    public boolean deleteComment(Long id){ return comments.remove(id) != null; }

    public List<Comment> findCommentsByArticle(Long articleId){
        return comments.values().stream()
                .filter(c -> Objects.equals(c.getArticleId(), articleId))
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .collect(Collectors.toList());
    }

    public int countCommentsByArticle(Long articleId){
        return (int) comments.values().stream()
                .filter(c -> Objects.equals(c.getArticleId(), articleId))
                .count();
    }
}