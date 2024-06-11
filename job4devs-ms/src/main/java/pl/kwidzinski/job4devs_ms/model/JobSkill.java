package pl.kwidzinski.job4devs_ms.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class JobSkill {

    @Id
    private String skillId;
    private String skill;
    private String level;

    public JobSkill(String skill, String level) {
        this.skill = skill;
        this.level = level;
    }
}
