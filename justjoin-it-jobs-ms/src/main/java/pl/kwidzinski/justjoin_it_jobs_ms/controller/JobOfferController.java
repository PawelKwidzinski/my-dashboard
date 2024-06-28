package pl.kwidzinski.justjoin_it_jobs_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.kwidzinski.justjoin_it_jobs_ms.model.JobOffer;
import pl.kwidzinski.justjoin_it_jobs_ms.service.JobService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JobOfferController {

    private final JobService jobService;

    @GetMapping("/justJoinIt/{location}/{technology}/{experience}")
    public List<String> getDevJobs(@PathVariable("location") String location, @PathVariable("technology") String technology,
                                   @PathVariable("experience") String experience, @RequestParam(required = false) String remote) {
        return jobService.getJustJoinItJobUrls(location, technology, experience, remote);
    }

    @GetMapping("/justJoinIt/all-offers")
    List<JobOffer> getAllJobs() {
        return jobService.getAllJobOffers();
    }

    @GetMapping("/justJoinIt/date/{date}")
    List<JobOffer> getAllJobOffersByDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return jobService.getAllJobOffersByDate(parsedDate);
    }

    @GetMapping("/justJoinIt/experience/{experience}")
    List<JobOffer> getAllJobOffersByExperience(@PathVariable String experience) {
        return jobService.getAllJobOffersByExperience(experience);
    }

}
