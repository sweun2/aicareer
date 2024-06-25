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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        User user = userService.getLoginUser();
        CommunityVote communityVote = CommunityVote.builder()
                .title(votePost.getTitle())
                .description(votePost.getDescription())
                .isMultiple(votePost.getIsMultiple())
                .isAnonymous(votePost.getIsAnonymous())
                .voteOption(new ArrayList<>())
                .endDate(LocalDateTimeStringConverter.StringToLocalDateTime(votePost.getEndDate()))
                .voteUser(new ArrayList<>())
                .build();

        votePost.getVoteOption().forEach(option -> {
            addVoteOption(option.trim(), communityVote);
        });

        if(communityVote.getVoteOption().size()<2)
            throw new BusinessException(ResponseErrorCode.VOTE_OPTION_BAD_REQUEST);

        return communityVoteRepository.save(communityVote);
    }
    public CommunityVote updateVote(CommunityVoteRequestDto.VotePost votePost, Integer voteId) {
        log.info("updateVote");

        CommunityVote communityVote = communityVoteRepository.findById(voteId).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );

        // Update the basic fields
        communityVote.setTitle(votePost.getTitle());
        communityVote.setDescription(votePost.getDescription());
        communityVote.setIsMultiple(votePost.getIsMultiple());
        communityVote.setIsAnonymous(votePost.getIsAnonymous());
        communityVote.setEndDate(LocalDateTimeStringConverter.StringToLocalDateTime(votePost.getEndDate()));

        Set<VoteOption> existingOptions = new HashSet<>(communityVote.getVoteOption());
        Set<String> newOptionTexts = votePost.getVoteOption().stream().map(String::trim).collect(Collectors.toSet());
        Set<VoteOption> optionsToRemove = existingOptions.stream()
                .filter(option -> !newOptionTexts.contains(option.getOption().trim()))
                .collect(Collectors.toSet());

        optionsToRemove.forEach(option -> deleteVoteOption(option, communityVote));
        newOptionTexts.removeAll(existingOptions.stream().map(VoteOption::getOption).toList());
        newOptionTexts.forEach(option -> {
            addVoteOption(option.trim(), communityVote);
        });

        if(communityVote.getVoteOption().size()<2)
            throw new BusinessException(ResponseErrorCode.VOTE_OPTION_BAD_REQUEST);

        return communityVoteRepository.save(communityVote);
    }


    public void addVoteOption(String option, CommunityVote communityVote) {
        VoteOption voteOption = VoteOption.builder()
                .option(option)
                .communityVote(communityVote)
                .voteCnt(0)
                .build();
        communityVote.getVoteOption().add(voteOption);
    }
    public void deleteVoteOption(VoteOption voteOption, CommunityVote communityVote) {
        communityVote.getVoteOption().remove(voteOption);
    }
    public void deleteVote(CommunityVote communityVote) {
        communityVoteRepository.delete(communityVote);
    }
    public void deleteVoteById(Integer voteId) {
        CommunityVote communityVote = communityVoteRepository.findById(voteId).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        communityVoteRepository.delete(communityVote);
    }

    public CommunityVote voteCasting(CommunityVoteRequestDto.CastVoteOption castVoteOption) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(castVoteOption.getPostingUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(communityPosting.getCommunityVote() == null) {
            throw new BusinessException(ResponseErrorCode.VOTE_NOT_FOUND);
        } else {
            CommunityVote communityVote = communityPosting.getCommunityVote();
            User user = userService.getLoginUser();
            communityVote.getVoteUser().forEach(voteUser -> {
                if(voteUser.getUser().equals(user)) {
                    throw new BusinessException(ResponseErrorCode.VOTE_ALREADY_CASTED);
                }
            });

            castVoteOption.getVoteOptionId().forEach(id -> {
                VoteOption voteOption = voteOptionRepository.findById(id).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.VOTE_OPTION_NOT_FOUND)
                );
                voteOption.setVoteCnt(voteOption.getVoteCnt()+1);
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
    public CommunityVote updateVoteCasting(CommunityVoteRequestDto.CastVoteOption castVoteOption) {
        CommunityPosting communityPosting = communityPostingRepository.findByUid(castVoteOption.getPostingUid()).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
        if(communityPosting.getCommunityVote() == null) {
            throw new BusinessException(ResponseErrorCode.VOTE_NOT_FOUND);
        } else {
            CommunityVote communityVote = communityPosting.getCommunityVote();
            User user = userService.getLoginUser();
            List<VoteUser> voteUserList = voteUserRepository.findByUserAndCommunityVote(user,communityVote);
            communityVote.getVoteUser().forEach(voteUser -> voteUser.getVoteOption().setVoteCnt(voteUser.getVoteOption().getVoteCnt()-1));
            communityVote.getVoteUser().removeAll(voteUserList);

            castVoteOption.getVoteOptionId().forEach(id -> {
                VoteOption voteOption = voteOptionRepository.findById(id).orElseThrow(
                        () -> new BusinessException(ResponseErrorCode.VOTE_OPTION_NOT_FOUND)
                );
                voteOption.setVoteCnt(voteOption.getVoteCnt()+1);
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
    public CommunityVote cancelVoteCasting(CommunityVoteRequestDto.CastVoteOption castVoteOption) {
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
    public CommunityVote getVoteById(Integer voteId) {
        return communityVoteRepository.findById(voteId).orElseThrow(
                () -> new BusinessException(ResponseErrorCode.UID_NOT_FOUND)
        );
    }
}
