package pl.mydashboard.tricity_news_ms.service.impl;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.mydashboard.tricity_news_ms.model.Article;
import pl.mydashboard.tricity_news_ms.repository.ArticleRepository;
import pl.mydashboard.tricity_news_ms.service.IArticleService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements IArticleService {

    private final ArticleRepository articleRepository;

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);
    private static final String MAIN_URL = "https://www.trojmiasto.pl/wiadomosci/";
    private static final String URL_CSS_QUERY = "div.newsList > article > div.newsList__img > a[href]";
    private static final String DESCRIPTION_CSS_QUERY = "div.newsList > article > div.newsList__content > p.newsList__desc";
    private static final String TAG_CSS_QUERY = "div.newsList > article > div.newsList__content > div.newsList__tag > a";

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public List<Article> getAllArticlesByDate(LocalDate date) {
        return articleRepository.findAllByDate(date);
    }

    @Override
    public List<Article> getAllArticlesByTag(String tag) {
        return articleRepository.findAllByTag(tag);
    }

    @Scheduled(fixedDelay = 3_600_000)
    @Override
    public void save() {
        List<String> articleUrls = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> tags = new ArrayList<>();

        try {
            // Download page content
            Document document = Jsoup.connect(MAIN_URL).get();
            // List all article links from the site
            Elements urlsElements = document.select(URL_CSS_QUERY);
            Elements descElements = document.select(DESCRIPTION_CSS_QUERY);
            Elements tagElements = document.select(TAG_CSS_QUERY);

            for (Element element : urlsElements) {
                String url = element.attr("href");
                // find url duplicates
                boolean matchedUrl = articleUrls.stream()
                        .anyMatch(u -> u.equals(url));
                if (!matchedUrl) {
                    articleUrls.add(url);
                }
            }
            for (int i = 0; i < descElements.size(); i++) {
                String description = descElements.get(i).childNodes().getFirst().toString();
                String tag = tagElements.get(i).childNodes().getFirst().toString().trim();
                // find description duplicates
                boolean matchedDescription = descriptions.stream()
                        .anyMatch(d -> d.equals(description));
                if (!matchedDescription) {
                    descriptions.add(description);
                    tags.add(tag);
                }
            }
        } catch (IOException e) {
            log.error("Error during fetching data. {}", e.toString());
        }

        for (int i = 0; i < articleUrls.size(); i++) {
            String url = articleUrls.get(i);
            String description = descriptions.get(i);
            String tag = tags.get(i);

            Article article = new Article(url, description, tag.trim().toLowerCase(), LocalDate.now());
            Optional<Article> optUrlArticle = articleRepository.findByUrl(article.getUrl());
            optUrlArticle.ifPresentOrElse(
                    existingArticle -> log.info("Article urls already exists: {}", existingArticle.getUrl()),
                    () -> {
                        articleRepository.save(article);
                        log.info("Article saved: {}", article);
                    }
            );
        }
    }

}
