package pl.kwidzinski.job4devs_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kwidzinski.job4devs_ms.model.JobOffer;
import pl.kwidzinski.job4devs_ms.service.JobService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class JobOfferController {
    private final JobService jobService;

    @GetMapping("/justJoinIt")
    public List<JobOffer> getDevJobs() {
        jobService.saveJobOffers();
        return jobService.getAllJobOffers();
    }

}
