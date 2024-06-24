package co.unlearning.aicareer.domain.community.communityvote.service;

import co.unlearning.aicareer.domain.common.user.User;
import co.unlearning.aicareer.domain.common.user.service.UserService;
import co.unlearning.aicareer.domain.community.communityposting.CommunityPosting;
import co.unlearning.aicareer.domain.community.communityposting.repository.CommunityPostingRepository;
import co.unlearning.aicareer.domain.community.communityvote.CommunityVote;
import co.unlearning.aicareer.domain.community.communityvote.VoteOption;
import co.unlearning.aicareer.domain.community.communityvote.VoteUser;
import co.unlearning.aicareer.domain.community.communityvote.dto.CommunityVoteRequestDto;
import co.unlearning.aicareer.domain.community.communityvote.repository.CommunityVoteRepository;
import co.unlearning.aicareer.domain.community.communityvote.repository.VoteOptionRepository;
import co.unlearning.aicareer.domain.community.communityvote.repository.VoteUserRepository;
import co.unlearning.aicareer.global.utils.converter.LocalDateTimeStringConverter;
import co.unlearning.aicareer.global.utils.error.code.ResponseErrorCode;
import co.unlearning.aicareer.global.utils.error.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CommunityVoteService {
    private final CommunityVoteRepository communityVoteRepository;
    private final CommunityPostingRepository communityPostingRepository;
    private final VoteUserRepository voteUserRepository;
    private final UserService userService;
    private final VoteOptionRepository voteOptionRepository;
    public CommunityVote createVote(CommunityVoteRequestDto.VotePost votePost) {
        log.info("createVote");
        CommunityVote communityVote = CommunityVote.builder()
                .title(votePost.getTitle())
                .description(votePost.getDescription())
                .isMultiple(votePost.getIsMultiple())
                .isAnonymous(votePost.getIsAnonymous())
                .voteOption(new ArrayList<>())
                .endDate(LocalDateTimeStringConverter.StringToLocalDateTime(votePost.getEndDate()))
                .voteUser(new ArrayList<>())
                .build();

        communityVote.setCommunityPosting(communityPostingRepository.findByUid(votePost.getPostingUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        ));

        votePost.getVoteOption().forEach(option -> {
            addVoteOption(option, communityVote);
        });


        return communityVoteRepository.save(communityVote);
    }

    public void addVoteOption(String option, CommunityVote communityVote) {
        VoteOption voteOption = VoteOption.builder()
                .option(option)
                .communityVote(communityVote)
                .build();
        communityVote.getVoteOption().add(voteOption);
    }
    public void deleteVoteOption(VoteOption voteOption, CommunityVote communityVote) {
        communityVote.getVoteOption().remove(voteOption);
    }
    public void deleteVote(CommunityVote communityVote) {
        communityVoteRepository.delete(communityVote);
    }

    public CommunityVote castVote(CommunityVoteRequestDto.CastVoteOption castVoteOption) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(castVoteOption.getPostingUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(communityPosting.getCommunityVote() == null) {
            throw new BusinessException(ResponseErrorCode.VOTE_NOT_FOUND);
        } else {
            CommunityVote communityVote = communityPosting.getCommunityVote();
            User user = userService.getLoginUser();
            if(communityVote.getVoteUser().stream().anyMatch(voteUser -> voteUser.getUser().equals(user))) {
                throw new BusinessException(ResponseErrorCode.VOTE_ALREADY_CASTED);
            }

            castVoteOption.getVoteOptionId().forEach(id -> {
                VoteOption voteOption = voteOptionRepository.findById(id).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.VOTE_OPTION_NOT_FOUND)
                );
                VoteUser voteUser = VoteUser.builder()
                        .user(user)
                        .communityVote(communityVote)
                        .voteOption(voteOption)
                        .build();
                communityVote.getVoteUser().add(voteUser);
            });
            return communityVoteRepository.save(communityVote);
        }
    }
    public CommunityVote updateCastVote(CommunityVoteRequestDto.CastVoteOption castVoteOption) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(castVoteOption.getPostingUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(communityPosting.getCommunityVote() == null) {
            throw new BusinessException(ResponseErrorCode.VOTE_NOT_FOUND);
        } else {
            CommunityVote communityVote = communityPosting.getCommunityVote();
            User user = userService.getLoginUser();
            List<VoteUser> voteUserList = voteUserRepository.findByUserAndCommunityVote(user,communityVote);
            communityVote.getVoteUser().removeAll(voteUserList);

            castVoteOption.getVoteOptionId().forEach(id -> {
                VoteOption voteOption = voteOptionRepository.findById(id).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.VOTE_OPTION_NOT_FOUND)
                );
                VoteUser voteUser = VoteUser.builder()
                        .user(user)
                        .communityVote(communityVote)
                        .voteOption(voteOption)
                        .build();
                communityVote.getVoteUser().add(voteUser);
            });
            return communityVoteRepository.save(communityVote);
        }

    }
    public CommunityVote cancelVote(CommunityVoteRequestDto.CastVoteOption castVoteOption) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(castVoteOption.getPostingUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(communityPosting.getCommunityVote() == null) {
            throw new BusinessException(ResponseErrorCode.VOTE_NOT_FOUND);
        } else {
            CommunityVote communityVote = communityPosting.getCommunityVote();
            User user = userService.getLoginUser();
            if(communityVote.getVoteUser().stream().noneMatch(voteUser -> voteUser.getUser().equals(user))) {
                throw new BusinessException(ResponseErrorCode.VOTE_ALREADY_CASTED);
            }

            castVoteOption.getVoteOptionId().forEach(id -> {
                VoteOption voteOption = voteOptionRepository.findById(id).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.VOTE_OPTION_NOT_FOUND)
                );
                voteUserRepository.findByUserAndCommunityVoteAndVoteOption(user, communityVote, voteOption).ifPresentOrElse(voteUserRepository::delete, () -> {
                    throw new BusinessException(ResponseErrorCode.VOTE_ALREADY_CANCEL);
                });
            });
            return communityVoteRepository.save(communityVote);
        }
    }

}
