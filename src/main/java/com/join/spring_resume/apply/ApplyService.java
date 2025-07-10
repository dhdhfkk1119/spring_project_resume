package com.join.spring_resume.apply;

import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.resume.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplyService {

    private final ApplyRepository applyRepository;

    // 지원하기
    @Transactional
    public Apply applySave(ApplyRequest.SaveDTO saveDTO, Recruit recruit , Resume resume){
        return applyRepository.save(saveDTO.toEntity(recruit,resume));
    }

    // 지원한지 내역 체크
    public Boolean isAppliedCheck(Long memberIdx,Long recruitIdx){
        return applyRepository.existsByMemberIdxAndRecruitIdx(memberIdx, recruitIdx);
    }
    
    // 자신이 지원한 공고를 가져오기
    public Page<Apply> MyApplyList(Long idx, Pageable pageable){
       return applyRepository.findAllAppliesByMemberIdx(idx,pageable);
    }

    // 자신이 지원한 공고에 대한 유저 정보를 가져오기
    public Page<Apply> getApplicantsForRecruit(Long recruitIdx,Pageable pageable) {
        return applyRepository.findAllByRecruitIdx(recruitIdx,pageable);
    }

    // 내가 자신한 공고에
    public Long getRecruitCount(Long memberIdx) {
        return applyRepository.countByMemberIdx(memberIdx);
    }

}
