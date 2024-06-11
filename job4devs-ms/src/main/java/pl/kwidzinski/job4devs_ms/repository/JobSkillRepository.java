package pl.kwidzinski.job4devs_ms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.kwidzinski.job4devs_ms.model.JobSkill;

import java.util.List;

public interface JobSkillRepository extends MongoRepository<JobSkill, String> {
    List<JobSkill> findBySkillAndLevel(String skill, String level);
}
