package ec.eureka.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UereSiverApplication {

	public static void main(String[] args) {
		SpringApplication.run(UereSiverApplication.class, args);
	}
}
