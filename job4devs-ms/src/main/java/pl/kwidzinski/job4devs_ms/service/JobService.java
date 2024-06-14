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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private final JobOfferRepository jobOfferRepository;
    private final JobSkillRepository jobSkillRepository;
    private static final String MAIN_URL = "https://justjoin.it";

    private List<String> getJobUrls(String location, String technology) {
        List<String> jobUrls = new ArrayList<>();

        try {
            // Download page content
            Document document = Jsoup.connect(String.format("%s/%s/%s", MAIN_URL, location, technology)).get();

            // List all job links from the site
            Elements urlsElement = document.select("a[href]");
            // i = 25, because the previous indexes do not give any useful urls
            for (int i = 25; i < urlsElement.size(); i++) {
                jobUrls.add(urlsElement.get(i).attr("href"));
            }

        } catch (IOException e) {
            log.error("Error during fetching data. {}", e.toString());
        }
        return jobUrls;
    }

    public void saveJobOffers(String location, String technology) {
        List<String> jobUrls = getJobUrls(location, technology);

        try {
            for (int i = 0; i < 5; i++) {
                String jobUrl = MAIN_URL + jobUrls.get(i);
                log.debug("Job Link {}: ", jobUrl);

                JobOffer jobOffer = new JobOffer();
                jobOffer.setUrl(jobUrl);
                jobOffer.setCreationTime(LocalDateTime.now());

                String employerName = "";
                String workplace = "";
                String position = "";

                List<JobSkill> jobSkillsToSave = new ArrayList<>();
                List<JobSkill> jobSkillsFoundedInDb = new ArrayList<>();

                List<String> skills = new ArrayList<>();
                List<String> skillsLevel = new ArrayList<>();

                // Download page content
                Document document = Jsoup.connect(jobUrl).get();

                // Get all skills and levels from the job offer (jobUrl)
                getStringsFromTechStack(document, "h6.MuiTypography-root.MuiTypography-subtitle2", skillsLevel);
                getStringsFromTechStack(document, "span.MuiTypography-root.MuiTypography-subtitle4", skills);
                
                // WORKING employers name
                Elements employersElement = document.select("div.css-u51ts9");

                for (Element employer : employersElement) {
                    employerName = employer.childNodes().get(1).childNodes().get(1).toString();
                    log.info("Employer Name= {}: ", employerName);
                }
                //get workplace from job offer
                workplace = getStringFromHtmlDocument(document, "div.css-1seeldo", workplace, "Work Place= {}: ");
                //get position from job offer
                position = getStringFromHtmlDocument(document, "div.css-vb54bv > h1", position, "Position Name= {}: ");


                // skills and skillLevel always in the same size in Array list
                for (int j = 0; j < skills.size(); j++) {
                    String skill = skills.get(j);
                    String level = skillsLevel.get(j);

                    // checking if skill and skillLevel exist in database
                    List<JobSkill> jobSkillsFromDb = jobSkillRepository.findBySkillAndLevel(skill, level);

                    if (jobSkillsFromDb.isEmpty()) {
                        jobSkillsToSave.add(new JobSkill(skill, level));
                        log.info("Added new JobSkill  {}", new JobSkill(skill, level));
                    } else {
                        jobSkillsFoundedInDb.addAll(jobSkillsFromDb);
                        log.info("Job Skill already exist= {}", new JobSkill(skill, level));
                    }
                }

                jobSkillsToSave.addAll(jobSkillsFoundedInDb);
                jobSkillRepository.saveAll(jobSkillsToSave);
                jobOffer.setJobSkills(jobSkillsToSave);
                jobOffer.setTechnology(technology);
                jobOffer.setEmployer(employerName.trim());
                jobOffer.setWorkplace(workplace.trim());
                jobOffer.setPosition(position.trim());

                jobOfferRepository.findByUrl(jobUrl).ifPresentOrElse(o -> log.info("Url already exist= {}", jobUrl),
                        () -> {
                            jobOfferRepository.save(jobOffer);
                            log.info("Added new job offer {}", jobOffer);
                        });
            }
        } catch (IOException e) {
            log.error("Error during fetching data. {}", e.toString());
        }
    }

    private static void getStringsFromTechStack(Document document, String cssQuery, List<String> skillsLevel) {
        Elements skillElements = document.select(cssQuery);
        for (Element element : skillElements) {
            String string = String.valueOf(element.childNodes().getFirst());
            skillsLevel.add(string);
        }
    }

    private static String getStringFromHtmlDocument(Document document, String cssQuery, String htmlString, String logInfo) {
        Elements workplacesElement = document.select(cssQuery);

        for (Element workplaceElement : workplacesElement) {
            htmlString = workplaceElement.childNodes().getFirst().toString();
            log.info(logInfo, htmlString);
        }
        return htmlString;
    }

    public List<JobOffer> getAllJobOffers() {
        return jobOfferRepository.findAll();
    }
}
