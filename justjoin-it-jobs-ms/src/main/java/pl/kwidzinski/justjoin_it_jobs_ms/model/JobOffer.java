package pl.kwidzinski.justjoin_it_jobs_ms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Document
public class JobOffer {

    @Id
    private String id;
    @Indexed(unique = true)
    private String url;
    private String technology;
    private String location;
    private String experience;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate created;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate lastAppearance;
    private Boolean remote;
}
