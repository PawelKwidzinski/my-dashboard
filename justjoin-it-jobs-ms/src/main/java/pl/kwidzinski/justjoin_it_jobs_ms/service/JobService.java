package pl.kwidzinski.justjoin_it_jobs_ms.service;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.kwidzinski.justjoin_it_jobs_ms.model.JobOffer;
import pl.kwidzinski.justjoin_it_jobs_ms.repository.JobOfferRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobOfferRepository jobOfferRepository;

    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private static final String MAIN_URL = "https://justjoin.it";
    
    @Value("${justjoinit.param.location.trojmiasto}")
    private  String trojmiasto;
    @Value("${justjoinit.param.location.all-locations}")
    private  String allLocations;
    @Value("${justjoinit.param.technology.java}")
    private  String java;
    @Value("${justjoinit.param.experience.junior}")
    private  String junior;
    @Value("${justjoinit.param.experience.mid}")
    private  String mid;
    @Value("${justjoinit.param.experience.senior}")
    private  String senior;
    @Value("${justjoinit.param.remote}")
    private  String remote;

    @Scheduled(cron = "* 47 12 * * MON-FRI")
    public void saveJobsOffer() {
        // remote job urls from ALL LOCATION for JUNIOR java devs
        List<String> allLocationJuniorJavaJobUrls = getJustJoinItJobUrls(allLocations, java, junior, remote);
        saveJustJoinItJobsOffer(allLocationJuniorJavaJobUrls, allLocations, java, junior, remote);

        // remote job urls from ALL LOCATION for MID java devs
        List<String> allLocationMidJavaJobUrls = getJustJoinItJobUrls(allLocations, java, mid, remote);
        saveJustJoinItJobsOffer(allLocationMidJavaJobUrls, allLocations, java, mid, remote);

        // remote job urls from 3CITY for JUNIOR java devs
        List<String> homeOfficeTrojmiastoJuniorJavaJobUrls = getJustJoinItJobUrls(trojmiasto, java, junior, remote);
        saveJustJoinItJobsOffer(homeOfficeTrojmiastoJuniorJavaJobUrls, trojmiasto, java, junior, remote);

        // remote job urls from 3CITY for MID java devs
        List<String> homeOfficeTrojmiastoMidJavaJobUrls = getJustJoinItJobUrls(trojmiasto, java, mid, remote);
        saveJustJoinItJobsOffer(homeOfficeTrojmiastoMidJavaJobUrls, trojmiasto, java, mid, remote);

        // no remote job urls from 3CITY for JUNIOR java devs
        List<String> trojmiastoJuniorJavaJobUrls = getJustJoinItJobUrls(trojmiasto, java, junior, null);
        saveJustJoinItJobsOffer(trojmiastoJuniorJavaJobUrls, trojmiasto, java, junior, null);

        // no remote job urls from 3CITY for MID java devs
        List<String> trojmiastoJuniorMidJobUrls = getJustJoinItJobUrls(trojmiasto, java, mid, null);
        saveJustJoinItJobsOffer(trojmiastoJuniorMidJobUrls, trojmiasto, java, mid, null);

    }

    public List<String> getJustJoinItJobUrls(String location, String technology, String experience, String remote) {
        Document document;
        List<String> jobUrls = new ArrayList<>();

        if (remote != null) {
            try {
                // Download page content
                document = Jsoup.connect(String.format("%s/%s/%s/%s/%s", MAIN_URL, location, technology, experience, remote)).get();

                // List all job links from the site
                Elements urlsElement = document.select("a[href]");
                // i = 25, because the previous indexes do not give any useful urls
                for (int i = 25; i < urlsElement.size(); i++) {
                    jobUrls.add(urlsElement.get(i).attr("href"));
                }

            } catch (IOException e) {
                log.error("Error during fetching data. {}", e.toString());
            }
        } else {
            try {
                document = Jsoup.connect(String.format("%s/%s/%s/%s", MAIN_URL, location, technology, experience)).get();

                // List all job links from the site
                Elements urlsElement = document.select("a[href]");
                // i = 25, because the previous indexes do not give any useful urls
                for (int i = 25; i < urlsElement.size(); i++) {
                    jobUrls.add(urlsElement.get(i).attr("href"));
                }

            } catch (IOException e) {
                log.error("Error during fetching data. {}", e.toString());
            }
        }
        return jobUrls;
    }

    public List<JobOffer> getAllJobOffers() {
        return jobOfferRepository.findAll();
    }

    public List<JobOffer> getAllJobOffersByExperience(String experience) {
        return jobOfferRepository.findAllByExperience(experience);
    }

    public List<JobOffer> getAllJobOffersByDate(LocalDate date) {
        return jobOfferRepository.findAllByCreated(date);
    }

    private void saveJustJoinItJobsOffer(List<String> jobUrls, String location, String technology, String experience, String remote) {
        for (String jobUrl : jobUrls) {
            String fullJobUrl = MAIN_URL + jobUrl;
            log.info("Job Offer url: {}", fullJobUrl);

            JobOffer jobOffer = createJobOffer(fullJobUrl, location, technology, experience, remote);
            updateOrSaveJobOffer(jobOffer);
        }
    }

    private JobOffer createJobOffer(String url, String location, String technology, String experience, String remote) {
        JobOffer jobOffer = new JobOffer();
        jobOffer.setUrl(url);
        jobOffer.setCreated(LocalDate.now());
        jobOffer.setTechnology(technology);
        jobOffer.setLocation(location);
        jobOffer.setExperience(getExperienceLevel(experience));
        jobOffer.setRemote(isRemote(remote));
        return jobOffer;
    }

    private void updateOrSaveJobOffer(JobOffer jobOffer) {
        Optional<JobOffer> foundJobOffer = jobOfferRepository.findByUrl(jobOffer.getUrl());
        if (foundJobOffer.isPresent()) {
            updateJobOffer(foundJobOffer.get());
        } else {
            saveJobOffer(jobOffer);
        }
    }

    private void updateJobOffer(JobOffer existingJobOffer) {
        existingJobOffer.setLastAppearance(LocalDate.now());
        JobOffer updatedJobOffer = jobOfferRepository.save(existingJobOffer);
        log.info("Job Offer -> last appearance date updated: {}", updatedJobOffer);
    }

    private void saveJobOffer(JobOffer jobOffer) {
        JobOffer savedJobOffer = jobOfferRepository.save(jobOffer);
        log.info("New Job Offer added: {}", savedJobOffer);
    }

    private String getExperienceLevel(String experience) {
        return experience.substring(17);
    }

    private boolean isRemote(String remote) {
        return remote != null;
    }
}
