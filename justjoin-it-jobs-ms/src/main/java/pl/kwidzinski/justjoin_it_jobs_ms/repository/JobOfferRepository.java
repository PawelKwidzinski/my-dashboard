package pl.kwidzinski.justjoin_it_jobs_ms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.kwidzinski.justjoin_it_jobs_ms.model.JobOffer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobOfferRepository extends MongoRepository<JobOffer, String> {
    Optional<JobOffer> findByUrl(String url) ;
    List<JobOffer> findAllByCreated(LocalDate createdDate);
    List<JobOffer> findAllByExperience(String experience);
    List<JobOffer> findAllByTechnology(String technology);
    List<JobOffer> findAllByLocation(String location);
}
