package ua.kpi.realitu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.realitu.auth.enums.Role;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.enums.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID> {

    List<Article> findAllByAuthorId(UUID authorId);

    List<Article> findAllByCategory(Category category);

    Optional<Article> findByTitle(String title);

    Optional<Article> findFirstByOrderByCreationDateDesc();
}
