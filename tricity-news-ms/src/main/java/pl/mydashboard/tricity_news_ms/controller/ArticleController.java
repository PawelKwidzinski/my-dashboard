package pl.mydashboard.tricity_news_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mydashboard.tricity_news_ms.model.Article;
import pl.mydashboard.tricity_news_ms.service.IArticleService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ArticleController {
    private final IArticleService articleService;

    @GetMapping("/news")
    public List<Article> getAllArticles() {
        return articleService.getAllArticles();
    }
}
