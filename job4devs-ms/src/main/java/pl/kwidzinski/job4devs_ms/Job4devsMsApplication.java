package pl.kwidzinski.job4devs_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Job4devsMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Job4devsMsApplication.class, args);
	}

}
