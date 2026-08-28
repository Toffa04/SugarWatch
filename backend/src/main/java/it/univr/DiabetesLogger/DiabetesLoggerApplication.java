package it.univr.DiabetesLogger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DiabetesLoggerApplication extends SpringApplication{

	public static void main(String[] args) {
		SpringApplication.run(DiabetesLoggerApplication.class, args);
	}

}
