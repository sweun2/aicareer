package co.unlearning.aicareer.domain;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "recruitment", description = "체용 공고 api")
@RequiredArgsConstructor
@RequestMapping("/api/recruitment")
public class BaseErrorController {

}
