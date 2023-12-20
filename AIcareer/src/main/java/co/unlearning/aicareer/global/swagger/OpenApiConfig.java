package co.unlearning.aicareer.global.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(@Value("${spring-doc.version}") String appVersion) {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("accessToken");

        Info info = new Info().title("AI career API").version(appVersion)
                .description("에이아이커리어 api server 입니다.")
                //.termsOfService("http://swagger.io/terms/")
                .contact(new Contact().name("sweun2").url("https://github.com/sweun2").email("sweun3@gmail.com"));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .info(info);
    }

}