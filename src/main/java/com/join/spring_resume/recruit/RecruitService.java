package com.join.spring_resume.recruit;

import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.session.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;

    // 공고 등록하기
    @Transactional
    public void recruit(RecruitRequest.RecruitDTO recruitDTO, Corp corp){
        recruitDTO.validate();
        Recruit recruit = recruitDTO.toEntity(corp);
        recruitRepository.save(recruit);
    }

    // 공고 삭제
    @Transactional
    public void recruitDelete(Long idx){
        Recruit recruit = recruitRepository.findByRecruitIdx(idx)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을수 없습니다"));
        recruitRepository.delete(recruit);
    }

    // 공고 수정
    @Transactional
    public void recruitUpdate(Long idx,RecruitRequest.RecruitUpdateDTO recruitDTO){
        Recruit recruit = recruitRepository.findByRecruitIdx(idx)
                .orElseThrow(() -> new RuntimeException("해당 유저를 찾을수 없습니다"));

        recruit.setRecruitTitle(recruitDTO.getRecruitTitle());
        recruit.setArea(recruitDTO.getArea());
        recruit.setRecruitNumber(recruitDTO.getRecruitNumber());
        recruit.setRecruitContent(recruitDTO.getRecruitContent());
        recruit.setStartAt(recruitDTO.getStartAt().toLocalDateTime());
        recruit.setEndAt(recruitDTO.getEndAt().toLocalDateTime());

    }

    // 등록된 모든 공고 보기
    public List<Recruit> findAllList(){
        return recruitRepository.findAll();
    }


}
