package pl.kwidzinski.job4devs_ms.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.kwidzinski.job4devs_ms.model.JobOffer;
import pl.kwidzinski.job4devs_ms.model.JobSkill;
import pl.kwidzinski.job4devs_ms.repository.JobOfferRepository;
import pl.kwidzinski.job4devs_ms.repository.JobSkillRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private final JobOfferRepository jobOfferRepository;
    private final JobSkillRepository jobSkillRepository;
    private static final String TROJMIASTO_JAVA_URL = "https://justjoin.it/trojmiasto/java";
    private static final String MAIN_URL = "https://justjoin.it";

    private List<String> getJobUrls() {
        List<String> jobLinks = new ArrayList<>();

        try {
            // Download page content
            Document document = Jsoup.connect(TROJMIASTO_JAVA_URL).get();

            // List all job links from the site
            Elements links = document.select("a[href]");
            for (Element link : links) {
                jobLinks.add(link.attr("href"));
            }
        } catch (IOException e) {
            log.error("Error during fetching data. {}", e.toString());
        }
        return jobLinks;
    }

    public void saveJobOffers() {
        List<String> jobLinks = getJobUrls();

        // Index 0 from list is main ulr
        try {
            for (int i = 24; i < jobLinks.size(); i++) {
                String jobUrl = MAIN_URL + jobLinks.get(i);
                log.debug("Job Link {}: ", jobUrl);

                // Download page content
                Document document = Jsoup.connect(jobUrl).get();
                // List all skills and levels from the job offer (jobUrl)
                Elements skillElements = document.select("h6.MuiTypography-root.MuiTypography-subtitle2");
                Elements levelElements = document.select("span.MuiTypography-root.MuiTypography-subtitle4");

                List<JobSkill> jobSkills = new ArrayList<>();
                List<String> skills = new ArrayList<>();
                List<String> skillsLevel = new ArrayList<>();

                JobOffer jobOffer = new JobOffer();
                jobOffer.setLink(jobUrl);

                for (Element jobSkill : skillElements) {
                    String skill = String.valueOf(jobSkill.childNodes().getFirst());
                    skills.add(skill);
                }

                for (Element levelElement : levelElements) {
                    String level = String.valueOf(levelElement.childNodes().getFirst());
                    skillsLevel.add(level);
                }
                // skills and skillLevel always in the same size in Array list
                for (int j = 0; j < skills.size(); j++) {
                    String skill = skills.get(j);
                    String level = skillsLevel.get(j);

                    jobSkills.add(new JobSkill(skill,level));
                }

                jobSkillRepository.saveAll(jobSkills);
                jobOffer.setJobSkills(jobSkills);
                jobOfferRepository.save(jobOffer);
            }
        } catch (IOException e) {
            log.error("Error during fetching data. {}", e.toString());
        }
    }

    public List<JobOffer> getAllJobOffers() {
        return jobOfferRepository.findAll();
    }
}
