package com.week4.lucas.Article.repository;


import com.week4.lucas.Article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArticleRepository extends JpaRepository<Article,Long> {
    Page<Article> findAllByIsDeletedFalse(Pageable pageable);
}