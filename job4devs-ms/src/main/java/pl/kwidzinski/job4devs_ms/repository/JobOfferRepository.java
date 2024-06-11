package pl.kwidzinski.job4devs_ms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.kwidzinski.job4devs_ms.model.JobOffer;

import java.util.Optional;

@Repository
public interface JobOfferRepository extends MongoRepository<JobOffer, String> {
    Optional<JobOffer> findByUrl(String url) ;
}
