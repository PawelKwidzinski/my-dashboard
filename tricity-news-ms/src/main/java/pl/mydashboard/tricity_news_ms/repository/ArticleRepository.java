package pl.mydashboard.tricity_news_ms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.mydashboard.tricity_news_ms.model.Article;

import java.util.Optional;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {

    Optional<Article> findByUrl(String url);
}
