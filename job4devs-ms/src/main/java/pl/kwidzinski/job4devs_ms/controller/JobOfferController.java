package pl.kwidzinski.job4devs_ms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/justJoinIt/{location}/{technology}")
    public List<JobOffer> getDevJobs(@PathVariable("location") String location, @PathVariable("technology") String technology) {
        jobService.saveJobOffers(location, technology);
        return jobService.getAllJobOffers();
    }

}
