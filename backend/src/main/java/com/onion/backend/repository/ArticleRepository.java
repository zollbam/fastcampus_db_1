package com.onion.backend.repository;

import com.onion.backend.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId AND a.isDeleted = false ORDER BY a.createdDate DESC LIMIT 10")
    List<Article> findTop10ByBoardIdOrderByCreatedDateDesc(@Param("boardId") Long boardId);

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId AND a.id < :articleId AND a.isDeleted = false ORDER BY a.createdDate DESC LIMIT 10")
    List<Article> findTop10ByBoardIdAndArticleIdLessThanOrderByCreatedDateDesc(@Param("boardId") Long boardId,
                                                                               @Param("articleId") Long articleId);

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId AND a.id > :articleId AND a.isDeleted = false ORDER BY a.createdDate DESC LIMIT 10")
    List<Article> findTop10ByBoardIdAndArticleIdGreaterThanOrderByCreatedDateDesc(@Param("boardId") Long boardId,
                                                                                  @Param("articleId") Long articleId);

    @Query("SELECT a FROM Article a WHERE a.board.id = :boardId AND (a.id > :newArticleId and a.id < :oldArticleId) AND a.isDeleted = false ORDER BY a.createdDate DESC LIMIT 10")
    List<Article> findTop10ByBoardIdAndBetweenArticleIdThanOrderByCreatedDateDesc(@Param("boardId") Long boardId,
                                                                                  @Param("newArticleId") Long newArticleId,
                                                                                  @Param("oldArticleId") Long oldArticleId);

    @Query("SELECT a FROM Article a JOIN a.author u WHERE u.username = :username ORDER BY a.createdDate DESC LIMIT 1")
    Article findLatestArticleByAuthorUsernameOrderByCreatedDate(@Param("username") String username);

    @Query("SELECT a FROM Article a JOIN a.author u WHERE u.username = :username ORDER BY a.updatedDate DESC LIMIT 1")
    Article findLatestArticleByAuthorUsernameOrderByUpdatedDate(@Param("username") String username);


//    @Query("SELECT a FROM Article a WHERE a.createdDate >= :startDate AND a.createdDate < :endDate ORDER BY a.viewCount DESC LIMIT 1")
//    Article findHotArticle(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}