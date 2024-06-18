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
    private String technology;
    private LocalDateTime creationTime;
    private BasicInfo basicInfo;
    private JobDetails jobDetails;
    private List<JobSkill> jobSkills;

}
