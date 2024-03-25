package br.com.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@EnableJdbcAuditing
@SpringBootApplication
public class PicpayBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicpayBackendApplication.class, args);
	}

}
