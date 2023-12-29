package co.unlearning.aicareer.global.swagger;

import co.unlearning.aicareer.global.utils.error.ApiErrorCodeExample;
import co.unlearning.aicareer.global.utils.error.BaseErrorCode;
import co.unlearning.aicareer.global.utils.error.ErrorReason;
import co.unlearning.aicareer.global.utils.error.ErrorResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            ApiErrorCodeExample apiErrorCodeExample =
                    handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);
            // ApiErrorCodeExample 어노테이션 단 메소드 적용
            if (apiErrorCodeExample != null) {
                generateErrorCodeResponseExample(operation, apiErrorCodeExample.value());
            }
            return operation;
        };
    }
    private void generateErrorCodeResponseExample(
            Operation operation, Class<? extends BaseErrorCode> type) {
        ApiResponses responses = operation.getResponses();
        // 해당 이넘에 선언된 에러코드들의 목록을 가져옵니다.
        BaseErrorCode[] errorCodes = type.getEnumConstants();
        // 400, 401, 404 등 에러코드의 상태코드들로 리스트로 모읍니다.
        // 400 같은 상태코드에 여러 에러코드들이 있을 수 있습니다.
        Map<Integer, List<ExampleHolder>> statusWithExampleHolders =
                Arrays.stream(errorCodes)
                        .map(
                                baseErrorCode -> {
                                    try {
                                        ErrorReason errorReason = baseErrorCode.getErrorReason();
                                        return ExampleHolder.builder()
                                                .holder(
                                                        getSwaggerExample(
                                                                baseErrorCode.getExplainError(),
                                                                errorReason))
                                                .code(errorReason.getStatus())
                                                .name(errorReason.getCode())
                                                .build();
                                    } catch (NoSuchFieldException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        .collect(groupingBy(ExampleHolder::getCode));
        // response 객체들을 responses 에 넣습니다.
        addExamplesToResponses(responses, statusWithExampleHolders);
    }
    private void addExamplesToResponses(
            ApiResponses responses, Map<Integer, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach(
                (status, v) -> {
                    Content content = new Content();
                    MediaType mediaType = new MediaType();
                    // 상태 코드마다 ApiResponse을 생성합니다.
                    ApiResponse apiResponse = new ApiResponse();
                    //  List<ExampleHolder> 를 순회하며, mediaType 객체에 예시값을 추가합니다.
                    v.forEach(
                            exampleHolder -> mediaType.addExamples(
                                    exampleHolder.getName(), exampleHolder.getHolder()));
                    // ApiResponse 의 content 에 mediaType을 추가합니다.
                    content.addMediaType("application/json", mediaType);
                    apiResponse.setContent(content);
                    // 상태코드를 key 값으로 responses 에 추가합니다.
                    responses.addApiResponse(status.toString(), apiResponse);
                });
    }

    private Example getSwaggerExample(String value, ErrorReason errorReason) {
//ErrorResponse 는 클라이언트한 실제 응답하는 공통 에러 응답 객체입니다.
        ErrorResponse errorResponse = new ErrorResponse(errorReason, "요청시 패스정보입니다.");
        Example example = new Example();
        example.description(value);
        example.setValue(errorResponse);
        return example;
    }
}