package pl.kwidzinski.job4devs_ms.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class JobOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String link;

    @OneToMany
    @JoinColumn(name = "job_offer_fk")
    private List<JobSkill> jobSkills;

}
