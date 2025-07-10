package com.join.spring_resume.recruit_like;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitLikeService {

    private final RecruitLikeRepository recruitLikeRepository;

    // 공고 좋아요 누르기 (저장용)
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

    // 기업 좋아요 눌렸는지 체크하는 부분
    public boolean isCheckLike(Long memberId, Long recruitId) {
        return recruitLikeRepository.existsByMemberIdAndRecruitId(memberId, recruitId);
    }

    // 내가 좋아요 누른 공고의 목록 가져오기
    public Page<RecruitLike> recruitLikeList(Long idx, Pageable pageable){
        return recruitLikeRepository.findByMemberIdx(idx,pageable);
    }

}
