package pl.kwidzinski.justjoin_it_jobs_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JustjoinItJobsMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JustjoinItJobsMsApplication.class, args);
	}

}
