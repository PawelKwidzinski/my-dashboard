package pl.kwidzinski.job4devs_ms.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.kwidzinski.job4devs_ms.model.BasicInfo;
import pl.kwidzinski.job4devs_ms.model.JobOffer;
import pl.kwidzinski.job4devs_ms.model.JobDetails;
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

    private final JobOfferRepository jobOfferRepository;
    private final JobSkillRepository jobSkillRepository;

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private static final String MAIN_URL = "https://justjoin.it";

    @Value("${css.query.for.employer}")
    private String cssQueryForEmployerName;
    @Value("${css.query.for.workplace}")
    private String cssQueryForWorkplace;
    @Value("${css.query.for.position}")
    private String cssQueryForPosition;
    @Value("${css.query.for.detail}")
    private String cssQueryForJobDetail;
    @Value("${css.query.for.skill}")
    private String cssQueryForSkill;
    @Value("${css.query.for.level}")
    private String cssQueryForSkillLevel;

    public void saveJobOffers(String location, String technology) {
        List<String> jobUrls = getJobUrls(location, technology);

        try {
            for (int i = 0; i < jobUrls.size(); i++) {
                String jobUrl = MAIN_URL + jobUrls.get(i);
                log.info("Job Offer url: {}: ", jobUrl);

                JobOffer jobOffer = new JobOffer();
                jobOffer.setUrl(jobUrl);
                jobOffer.setCreationTime(LocalDateTime.now());

                List<JobSkill> skillsToSave = new ArrayList<>();
                List<JobSkill> skillsFoundedInDb = new ArrayList<>();

                // Download page content
                Document document = Jsoup.connect(jobUrl).get();

                // 1 Employer name, workplace, position
                // get employer name
                String employerName = getEmployerNameFromHtmlDocument(document, cssQueryForEmployerName);
                //get workplace from job offer
                String workplace = getStringFromHtmlDocument(document, cssQueryForWorkplace);
                //get position from job offer
                String position = getStringFromHtmlDocument(document, cssQueryForPosition);

                BasicInfo basicInfo = new BasicInfo(position.trim(), employerName.trim(), workplace.trim());

                log.info("Added new Basic Info to job offer{}", basicInfo);

                // 2 Job parameters from job offer
                JobDetails jobDetails = getJobParamFromHtmlDocument(document, cssQueryForJobDetail);
                log.info("Added new Job Details to job offer {}", basicInfo);

                // 3. All Tech stack skills and levels from the job offer (jobUrl)
                List<String> skills = getTechStackSkillsFromFromHtmlDocument(document, cssQueryForSkill);
                List<String> skillsLevel = getTechStackSkillsFromFromHtmlDocument(document, cssQueryForSkillLevel);

                // skills and skillLevel always in the same size in Array list
                for (int j = 0; j < skills.size(); j++) {
                    String skill = skills.get(j);
                    String level = skillsLevel.get(j);

                    // checking if skill and skillLevel exist in database
                    List<JobSkill> skillsFromDb = jobSkillRepository.findBySkillAndLevel(skill, level);

                    if (skillsFromDb.isEmpty()) {
                        skillsToSave.add(new JobSkill(skill, level));
                        log.info("Added new JobSkill to job offer {}", new JobSkill(skill, level));
                    } else {
                        skillsFoundedInDb.addAll(skillsFromDb);
                        log.info("Job Skill already exist in DB= {}", new JobSkill(skill, level));
                    }
                }

                skillsToSave.addAll(skillsFoundedInDb);
                jobSkillRepository.saveAll(skillsToSave);

                jobOffer.setTechnology(technology);
                jobOffer.setBasicInfo(basicInfo);
                jobOffer.setJobDetails(jobDetails);
                jobOffer.setJobSkills(skillsToSave);

                jobOfferRepository.findByUrl(jobUrl).ifPresentOrElse(o -> log.info("Url already exist in DB= {}", jobUrl),
                        () -> {
                            jobOfferRepository.save(jobOffer);
                            log.info("Added new Job Offer {}", jobOffer);
                        });
            }
        } catch (IOException e) {
            log.error("Error during fetching data. {}", e.toString());
        }
    }

    public List<JobOffer> getAllJobOffers() {
        return jobOfferRepository.findAll();
    }

    private static List<String> getJobUrls(String location, String technology) {
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

    private static String getEmployerNameFromHtmlDocument(Document document, String cssQuery) {
        Elements employerElements = document.select(cssQuery);
        String employerName = "";
        for (Element employer : employerElements) {
            employerName = employer.childNodes().get(1).childNodes().get(1).toString();
        }
        return employerName;
    }

    private static String getStringFromHtmlDocument(Document document, String cssQuery) {
        Elements workplacesElement = document.select(cssQuery);
        String htmlString = "";

        for (Element workplaceElement : workplacesElement) {
            htmlString = workplaceElement.childNodes().getFirst().toString();
        }
        return htmlString;
    }

    private static JobDetails getJobParamFromHtmlDocument(Document document, String cssQuery) {
        Elements jobDetailsElements = document.select(cssQuery);
        List<String> jobDetailsParameters = new ArrayList<>();

        for (int i = 1; i < jobDetailsElements.size(); i += 2) {
            String string = jobDetailsElements.get(i).childNodes().getFirst().toString();
            jobDetailsParameters.add(string.trim());
        }
        JobDetails jobDetails = new JobDetails();
        jobDetails.setTypeOfWork(jobDetailsParameters.get(0));
        jobDetails.setExperience(jobDetailsParameters.get(1));
        jobDetails.setEmploymentType(jobDetailsParameters.get(2));
        jobDetails.setOperatingMode(jobDetailsParameters.get(3));
        return jobDetails;
    }

    private static List<String> getTechStackSkillsFromFromHtmlDocument(Document document, String cssQuery) {
        Elements skillElements = document.select(cssQuery);
        List<String> techStackSkills = new ArrayList<>();
        for (Element element : skillElements) {
            String string = String.valueOf(element.childNodes().getFirst());
            techStackSkills.add(string.trim());
        }
        return techStackSkills;
    }

}
