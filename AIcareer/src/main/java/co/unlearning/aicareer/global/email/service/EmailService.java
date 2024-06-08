package co.unlearning.aicareer.global.email.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.UserInterest;
import co.unlearning.aicareer.domain.common.user.repository.UserRepository;
import co.unlearning.aicareer.domain.job.companytype.CompanyType;
import co.unlearning.aicareer.domain.job.education.Education;
import co.unlearning.aicareer.domain.job.recruitment.Recruitment;
import co.unlearning.aicareer.domain.job.recruitment.RecruitmentAddress;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentRepository;
import co.unlearning.aicareer.domain.job.recruitment.repository.RecruitmentSpecification;
import co.unlearning.aicareer.domain.job.recrutingjob.RecruitingJob;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static co.unlearning.aicareer.domain.job.recruitment.RecruitmentAddress.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
    @Value("${front-url}")
    private String frontUrl;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final RecruitmentRepository recruitmentRepository;
    @Async
    public void sendRecruitMailEveryDay() {
        if(Objects.equals(frontUrl, "https://alpha.aicareer.co.kr")) {
           throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
        }
        if (LocalDateTime.now().getDayOfWeek() == DayOfWeek.SATURDAY || LocalDateTime.now().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
        }
        Map<UserInterest, String> userUrlMap;
        if (LocalDateTime.now().getDayOfWeek() == DayOfWeek.MONDAY) {
            userUrlMap = getRecruitmentUrlMapWithDay(3,15);
        } else {
            userUrlMap = getRecruitmentUrlMapWithDay(1,15);
        }


        userUrlMap.forEach((userInterest, url) -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(userInterest.getReceiveEmail());
                helper.setSubject("에이아이 커리어 채용공고");

                String htmlContent = getRecruitmentMailHTML(url); // URL을 포함한 HTML 콘텐츠를 가져옵니다.
                helper.setText(htmlContent, true); // true는 HTML을 사용함을 의미합니다.

                javaMailSender.send(message);
            } catch (MessagingException e) {
                log.info(e.getMessage());
            }
        });
    }

    public String getRecruitmentMailHTML(String url) {
        String html = "";
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            String redirectUrl = Objects.requireNonNull(response.getHeaders().getLocation()).toString();

            // 리다이렉트 URL로부터 HTML 가져오기
            html = restTemplate.getForObject(redirectUrl, String.class);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return html;
    }

    public Map<UserInterest, String> getRecruitmentUrlMapWithDay(Integer day,Integer pageSize) {
        Map<UserInterest, String> userUrlMap = new HashMap<>();
        List<User> userList = userRepository.findAll();

        userList.forEach(user -> {
            if (user.getIsInterest() && user.getUserInterest() != null && user.getIsAgreeInformationTerms().getIsAgree()) {
                UserInterest userInterest = user.getUserInterest();

                // 수도권 여부에 따라 recruitmentAddress 설정
                Specification<Recruitment> specification = Specification.where(
                                RecruitmentSpecification.hasRecruitingJob(
                                        userInterest.getRecruitingJobSet().stream()
                                                .map(RecruitingJob::getRecruitJobName) // 직무 이름을 Enum 리스트로 변환
                                                .collect(Collectors.toList())
                                ))
                        .and(RecruitmentSpecification.hasCompanyType(
                                userInterest.getCompanyTypeSet().stream()
                                        .map(CompanyType::getCompanyTypeName) // 회사 유형 이름을 Enum 리스트로 변환
                                        .collect(Collectors.toList())
                        ))
                        .and(RecruitmentSpecification.hasEducation(
                                userInterest.getEducationSet().stream() // 가정: UserInterest에 이미 적절한 Enum 문자열이 저장되어 있음
                                        .map(Education::getDegree)
                                        .collect(Collectors.toList())
                        ))
                        .and(RecruitmentSpecification.isOpenRecruitment());

                LocalDateTime startOfYesterday = LocalDateTime.now().minusDays(day).withHour(0).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime endOfYesterday = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
                specification = specification.and(RecruitmentSpecification.uploadDateBetween(startOfYesterday, endOfYesterday));

                List<RecruitmentAddress> recruitmentAddresses = List.of(SEOUL, GANGNAM, MAPO, GURO_GARSAN, BUNDANG_PANGYO);
                if (!userInterest.getIsMetropolitanArea()) {
                    specification = specification.and(RecruitmentSpecification.hasRecruitmentAddress(List.of(RecruitmentAddress.values())));
                } else {
                    specification = specification.and(RecruitmentSpecification.hasRecruitmentAddress(recruitmentAddresses));
                }

                Sort sort = Sort.by("hits").descending();
                PageRequest pageableWithSort = PageRequest.of(0, pageSize, sort);
                List<Recruitment> resultList = recruitmentRepository.findAll(specification, pageableWithSort).stream().toList();
                if(!resultList.isEmpty()) {
                    String idList = resultList.stream()
                            .map(Recruitment::getUid)
                            .collect(Collectors.joining(","));

                    String initialUrl = "https://aicareer.co.kr/generator/mail?ids=" + idList;
                    if(userUrlMap.containsKey(userInterest) && userUrlMap.containsValue(initialUrl)) {
                        return;
                    } else {
                        userUrlMap.put(userInterest, initialUrl);
                    }
                }
            }
        });

        return userUrlMap;
    }

    @Async
    public void sendRecruitMailEveryWeek() {
        if(Objects.equals(frontUrl, "https://alpha.aicareer.co.kr")) {
            throw new BusinessException(ResponseErrorCode.INTERNAL_SERVER_ERROR);
        }
        Map<UserInterest, String> userUrlMap = getRecruitmentUrlMapWithDay(7,5);
        userUrlMap.forEach((userInterest, url) -> {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(userInterest.getReceiveEmail());
                helper.setSubject("에이아이 커리어 주간 탑5 조회수 채용공고");

                String htmlContent = getRecruitmentMailHTML(url);
                helper.setText(htmlContent, true);

                javaMailSender.send(message);
            } catch (MessagingException e) {
                log.info(e.getMessage());
            }
        });
    }

}