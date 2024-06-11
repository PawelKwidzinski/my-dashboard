package pl.kwidzinski.job4devs_ms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document
public class JobOffer {

    @Id
    private String id;
    @Indexed(unique = true)
    private String url;
    private String location;
    private String technology;
    private List<JobSkill> jobSkills;
    private LocalDateTime creationTime;

}
