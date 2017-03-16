package ec.eureka.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BuySiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuySiverApplication.class, args);
	}
}
