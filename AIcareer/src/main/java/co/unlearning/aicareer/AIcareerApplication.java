package co.unlearning.aicareer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
@OpenAPIDefinition(servers = {@Server(url = "https://api.aicareer.co.kr",description = "Default Server URL")})
@SpringBootApplication

public class AIcareerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AIcareerApplication.class, args);
	}
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder);
	}
}
