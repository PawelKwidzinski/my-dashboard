package pl.mydashboard.tricity_news_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.mydashboard.tricity_news_ms.model.Article;
import pl.mydashboard.tricity_news_ms.service.IArticleService;

import java.time.LocalDate;
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

    @GetMapping("/news/date/{date}")
    public List<Article> getArticlesByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        return articleService.getAllArticlesByDate(localDate);
    }

    @GetMapping("/news/tag/{tag}")
    public List<Article> getArticlesByTag(@PathVariable String tag) {
        return articleService.getAllArticlesByTag(tag);
    }
}
