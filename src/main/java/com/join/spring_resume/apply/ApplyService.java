package com.join.spring_resume.apply;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.resume.Resume;
import com.join.spring_resume.session.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    public List<Apply> MyApplyList(Long idx){
       return applyRepository.findAllAppliesByMemberIdx(idx);
    }

    // 자신이 지원한 공고에 대한 유저 정보를 가져오기
    public List<Apply> getApplicantsForRecruit(Long recruitIdx) {
        return applyRepository.findAllByRecruitIdx(recruitIdx);
    }
}
