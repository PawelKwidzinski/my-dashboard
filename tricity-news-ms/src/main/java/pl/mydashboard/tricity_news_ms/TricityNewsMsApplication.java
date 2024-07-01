package pl.mydashboard.tricity_news_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TricityNewsMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TricityNewsMsApplication.class, args);
	}

}
