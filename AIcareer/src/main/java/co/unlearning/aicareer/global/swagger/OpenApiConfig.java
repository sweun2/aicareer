package co.unlearning.aicareer.global.swagger;

import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExamples;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.code.ErrorReason;
import co.unlearning.aicareer.global.utils.error.dto.ErrorResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;

import static java.util.stream.Collectors.groupingBy;

@Component
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER).name("accessToken");

        Info info = new Info().title("AI career API").version("1.0.0")
                .description("에이아이커리어 api server 입니다.")
                //.termsOfService("http://swagger.io/terms/")
                .contact(new Contact().name("sweun2").url("https://github.com/sweun2").email("sweun3@gmail.com"));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .info(info);
    }
    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {

            ApiErrorCodeExamples apiErrorCodeExamples = handlerMethod.getMethodAnnotation(ApiErrorCodeExamples.class);
            if(apiErrorCodeExamples!=null) {
                Arrays.stream(apiErrorCodeExamples.value()).forEach(code ->{
                    generateErrorCodeResponseExample(operation, code.value());
                });
            }
            return operation;
        };
    }
    private void generateErrorCodeResponseExample(
            Operation operation, ResponseErrorCode type) {
        ApiResponses responses = operation.getResponses();

        ErrorReason errorReason = type.getErrorReason();
        ExampleHolder exampleHolder =  ExampleHolder.builder()
                .holder(
                        getSwaggerExample(
                                type.getExplainError(),
                                errorReason))
                .code(errorReason.getStatus())
                .name(errorReason.getCode())
                .build();
        // response 객체들을 responses 에 넣습니다.
        addExamplesToResponses(responses, exampleHolder);
    }
    private void addExamplesToResponses(ApiResponses responses, ExampleHolder exampleHolder) {
        Content content = new Content();
        MediaType mediaType = new MediaType();

        // 상태 코드마다 ApiResponse을 생성합니다.
        ApiResponse apiResponse = new ApiResponse();

        // ExampleHolder에 있는 값을 가져와 mediaType 객체에 예시값을 추가합니다.
        mediaType.addExamples(exampleHolder.getName(), exampleHolder.getHolder());

        // ApiResponse의 content에 mediaType을 추가합니다.
        content.addMediaType("application/json", mediaType);
        apiResponse.setContent(content);

        // 상태코드를 key 값으로 responses에 추가합니다.
        responses.addApiResponse(String.valueOf(exampleHolder.getCode()), apiResponse);
    }

    private Example getSwaggerExample(String value, ErrorReason errorReason) {
        ErrorResponse errorResponse = new ErrorResponse(errorReason);
        Example example = new Example();
        example.description(value);
        example.setValue(errorResponse);
        return example;
    }
}