package pl.mydashboard.tricity_news_ms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Document
public class Article {
    @Id
    private String id;
    private String url;
    private String description;
    private String tag;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    public Article(String url, String description, String tag, LocalDate date) {
        this.url = url;
        this.description = description;
        this.tag = tag;
        this.date = date;
    }
}
