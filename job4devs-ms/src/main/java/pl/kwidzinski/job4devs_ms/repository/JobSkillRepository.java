package pl.kwidzinski.job4devs_ms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kwidzinski.job4devs_ms.model.JobSkill;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {
}
