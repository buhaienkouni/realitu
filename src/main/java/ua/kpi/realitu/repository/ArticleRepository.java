package ua.kpi.realitu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.kpi.realitu.domain.Article;
import ua.kpi.realitu.domain.enums.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// TODO: Check if this is correct
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    List<Article> findAllByAuthorIdOrderByCreationDateDesc(UUID authorId);

    List<Article> findAllByCategoryOrderByCreationDateDesc(Category category);

    List<Article> findAllByOrderByCreationDateDesc();

    Optional<Article> findByTitle(String title);

    Optional<Article> findFirstByOrderByCreationDateDesc();
}
