package pl.mydashboard.tricity_news_ms.service;

import pl.mydashboard.tricity_news_ms.model.Article;

import java.util.List;

public interface IArticleService {
    void save();
    List<Article> getAllArticles();
}
