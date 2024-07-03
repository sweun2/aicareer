package co.unlearning.aicareer;

import co.unlearning.aicareer.domain.job.recruitmentbatch.GptService;
import co.unlearning.aicareer.domain.job.recruitmentbatch.service.RecruitmentBatchService;
import co.unlearning.aicareer.global.utils.MultipartFileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class GptServiceTest {

    @InjectMocks
    private GptService gptService;

    @Mock
    private RecruitmentBatchService recruitmentBatchService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    public void testExtractTextFromUrl() throws Exception {
        String url = "https://m.saramin.co.kr/job-search/view?rec_idx=48518518&is_apply_statics_auto_focus=&proposal_idx=0#seq=0";
        Document doc = Jsoup.connect(url).get();
        Elements images = doc.select("img");
        StringBuilder result = new StringBuilder();
        for (Element img : images) {
            String imgUrl = img.absUrl("src");
            if (!imgUrl.isEmpty()) {
                String fileExtension = recruitmentBatchService.getFileExtension(imgUrl);
                if (recruitmentBatchService.isValidImageFormat(fileExtension)) {
                    MultipartFile file = MultipartFileUtil.convertUrlToMultipartFile(imgUrl);
                    String ocrResult = recruitmentBatchService.performOcr(file, imgUrl);

                    JsonNode ocrResultJson = objectMapper.readTree(ocrResult);
                    JsonNode imagesNode = ocrResultJson.path("images");

                    for (JsonNode imageNode : imagesNode) {
                        JsonNode fieldsNode = imageNode.path("fields");
                        for (JsonNode fieldNode : fieldsNode) {
                            String inferText = fieldNode.path("inferText").asText();
                            result.append(inferText).append(" ");
                        }
                    }
                } else {
                    String text = img.attr("alt");
                    if (!text.isEmpty()) {
                        result.append(text).append(" ");
                    }
                }
            }
        }
        String pageText = doc.body().html();
        System.out.println(pageText);
        String title = doc.title();
        result.append(pageText);
    }
}
