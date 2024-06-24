package co.unlearning.aicareer.global.utils.error.code;

import co.unlearning.aicareer.global.utils.error.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ResponseErrorCode {
    @ExplainError("서버 내부 에러, 문의 바래요.")
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "COMMON_001", "서버 내부 에러"),
    @ExplainError("입력하는 enum 값에 대해 잘못된 값을 입력한 경우, 각 enum 값 철자를 확인 하세요. ")
    INVALID_ENUM_STRING_INPUT(HttpStatus.BAD_REQUEST.value(), "COMMON_002","잘못된 입력 값입니다."),
    @ExplainError("입력하는 date 값에 대해 입력 패턴이 틀린 경우, 'yyyy-MM-dd HH:mm' 형식 으로 입력해야 합니다.")
    INVALID_DATE_STRING_INPUT(HttpStatus.BAD_REQUEST.value(), "COMMON_003","잘못된 date 입력 값입니다."),
    @ExplainError("입력하는 date 값이 유효하지 않은 경우,마감 일자 입력은 현재 보다 나중이어야 합니다.")
    DATE_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "COMMON_004","잘못된 date 입력 값입니다."),
    @ExplainError("유저가 이미 추천을 했습니다.")
    USER_ALREADY_RECOMMEND(HttpStatus.METHOD_NOT_ALLOWED.value(), "COMMON_005","유저가 이미 추천을 했습니다."),
    @ExplainError("유저가 이미 추천 취소를 한 경우")
    USER_ALREADY_CANCEL_RECOMMEND(HttpStatus.METHOD_NOT_ALLOWED.value(), "COMMON_005","유저가 이미 추천을 했습니다."),
    @ExplainError("유저가 이미 신고를 했습니다.")
    USER_ALREADY_REPORT(HttpStatus.METHOD_NOT_ALLOWED.value(), "COMMON_006","유저가 이미 신고를 했습니다."),

    @ExplainError("DB에 유저 정보가 없는 경우, 회원 가입 필요")
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USER_001", "유저 정보가 없습니다."),
    @ExplainError("요청 end point에 대한 권한이 없는 경우, User role을 ADMIN 으로 변경 필요,DB에서 직접 변경해야 함")
    USER_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(),"USER_002","유저 권한이 부족합니다."),
    @ExplainError("access-token/refresh-token 정보가 잘못된 경우, 재로그인 필요")
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(),"USER_003","로그인에 실패했습니다."),
    @ExplainError("아이디 밴 당한 경우, 관리자에게 문의 바람")
    USER_BLOCKED(HttpStatus.UNAUTHORIZED.value(),"USER_004","접근이 차단되었습니다."),
    @ExplainError("유저 닉네임이 이미 존재하는 경우, 닉네임 변경 바람")
    USER_NICKNAME_DUPLICATE(HttpStatus.FORBIDDEN.value(),"USER_005","접근이 차단되었습니다."),


    @ExplainError("uid가 잘못되었을 때 발생하는 에러, uid 확인 필요")
    UID_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RECRUITMENT_001", "uid가 잘못 입력되었습니다."),
    @ExplainError("정렬 조건이 잘못 입력된 경우 ")
    SORT_CONDITION_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "RECRUITMENT_002", "sort condition 에러."),

    @ExplainError("북마크가 이미 추가되었습니다.")
    BOOKMARK_ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(), "BOOKMARK_001", "북마크가 이미 추가된 상태입니다."),


    @ExplainError("서버에 저장된 파일 위치와 입력한 파일 위치가 일치하지 않습니다.  '/home/app/img/test-image.jpg' 형식으로 입력해야 합니다. ")
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST.value(), "IMAGE_001","잘못된 image url 입력 값입니다."),
    @ExplainError("파일을 읽을 수 없습니다. 올바른 이미지 파일인지 확인해 주세요. ")
    NOT_FOUND_IMAGE_FILE(HttpStatus.NOT_FOUND.value(), "IMAGE_002","image 파일이 비어있습니다."),
    @ExplainError("이미지 파일이 형식 에러,이미지 파일 형식을 확인해 주세요. jpg, png 만 가능합니다. ")
    INVALID_IMAGE_CONTENT_TYPE(HttpStatus.BAD_REQUEST.value(), "IMAGE_003","잘못된 image 파일 형식입니다."),
    @ExplainError("잘못된 공고 이미지 입니다. 이미지를 다시 확인해 주세요.")
    INVALID_RECRUITMENT_IMAGE(HttpStatus.BAD_REQUEST.value(), "IMAGE_004","잘못된 공고 이미지 입니다."),
    @ExplainError("잘못된 커뮤니티 이미지 입니다. 이미지를 다시 확인해 주세요.")
    INVALID_COMMUNITY_IMAGE(HttpStatus.BAD_REQUEST.value(), "IMAGE_005","잘못된 커뮤니티 이미지 입니다."),


    @ExplainError("투표가 이미 종료된 경우, 투표를 다시 생성해 주세요.")
    VOTE_ALREADY_END(HttpStatus.BAD_REQUEST.value(), "VOTE_001", "투표가 이미 종료되었습니다."),
    @ExplainError("투표 선택지가 2개 미만인 경우, 투표 선택지를 2개 이상 입력해 주세요.")
    VOTE_OPTION_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "VOTE_002", "투표 선택지가 2개 미만입니다."),
    @ExplainError("투표가 존재하지 않습니다. 투표를 다시 생성해 주세요.")
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "VOTE_003", "투표가 존재하지 않습니다."),
    @ExplainError("투표 선택지가 존재하지 않습니다. 투표 선택지를 다시 생성해 주세요.")
    VOTE_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "VOTE_004", "투표 선택지가 존재하지 않습니다."),
    @ExplainError("이미 투표를 했습니다.")
    VOTE_ALREADY_CASTED(HttpStatus.BAD_REQUEST.value(), "VOTE_005", "이미 투표를 했습니다."),
    @ExplainError("이미 투표를 취소했습니다.")
    VOTE_ALREADY_CANCEL(HttpStatus.BAD_REQUEST.value(), "VOTE_006", "이미 투표를 취소했습니다."),
    ;
    private final int status;
    private final String code;
    private final String reason;

    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }
    public String getExplainError() {
        try {
            Field field = this.getClass().getDeclaredField(this.name());
            field.setAccessible(true);  // 필드에 접근할 수 있도록 설정
            ExplainError annotation = field.getAnnotation(ExplainError.class);
            return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
        } catch (NoSuchFieldException | SecurityException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
