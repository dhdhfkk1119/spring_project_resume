package com.join.spring_resume.recruit_like;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitLikeService {

    private final RecruitLikeRepository recruitLikeRepository;
    private final RecruitService recruitService;
    // 공고 좋아요 누르기
    @Transactional
    public boolean likeSaveToggle(RecruitLikeRequest.SaveDTO saveDTO,
                                  Member member,
                                  Recruit recruit) {
        Optional<RecruitLike> optionalLike = recruitLikeRepository.findByMemberAndRecruit(member, recruit);

        if (optionalLike.isPresent()) {
            recruitLikeRepository.delete(optionalLike.get());
            return false; // 삭제됨
        } else {
            recruitLikeRepository.save(saveDTO.toEntity(member,recruit));
            return true; // 저장됨
        }
    }

    public boolean isCheckLike(Long memberId, Long recruitId) {
        return recruitLikeRepository.existsByMemberIdAndRecruitId(memberId, recruitId);
    }

}
