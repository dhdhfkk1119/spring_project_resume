package com.join.spring_resume.recruit;

import com.join.spring_resume._core.errors.exception.Exception403;
import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume.apply.ApplyRepository;
import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.recruit_like.RecruitLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitLikeRepository recruitLikeRepository;
    private final ApplyRepository applyRepository;

    public Recruit findById(Long idx){
        return recruitRepository.findById(idx).orElseThrow(() -> new Exception404("해당 공고를찾을 수 없습니다"));
    }
    // 공고 등록하기
    @Transactional
    public void recruit(RecruitRequest.RecruitDTO recruitDTO, Corp corp){

        Recruit recruit = recruitDTO.toEntity(corp);
        recruitRepository.save(recruit);
    }

    // 공고 삭제
    @Transactional
    public void recruitDelete(Long idx){
        Recruit recruit = recruitRepository.findByRecruitIdx(idx)
                .orElseThrow(() -> new Exception404("해당 유저를 찾을수 없습니다"));

        if(recruit.isOwner(idx)){
            throw new Exception403("삭제권한이없습니다");
        }

        recruitRepository.delete(recruit);
    }

    // 공고 수정
    @Transactional
    public void recruitUpdate(Long idx,RecruitRequest.RecruitUpdateDTO recruitDTO){

        Recruit recruit = recruitRepository.findById(idx)
                .orElseThrow(() -> new Exception404("해당 게시물을 찾을 수 없습니다"));

        if(recruit.isOwner(idx)){
            throw new Exception403("수정권한이없습니다");
        }

        recruit.setRecruitTitle(recruitDTO.getRecruitTitle());
        recruit.setArea(recruitDTO.getArea());
        recruit.setRecruitNumber(recruitDTO.getRecruitNumber());
        recruit.setRecruitContent(recruitDTO.getRecruitContent());
        recruit.setStartAt(recruitDTO.getStartAt().atStartOfDay());
        recruit.setEndAt(recruitDTO.getEndAt().atStartOfDay());

    }

    // 등록된 모든 공고 보기 Page 형식
    public Page<Recruit> findAllList(Pageable pageable){
        return recruitRepository.findAll(pageable);
    }
    
    // 기존 공고 갯수 가져오기
    public Long countByRecruit(Long corpIdx){
        return recruitRepository.countByCorpIdx(corpIdx);
    }

    // 현재 로그인 한기업의 모든 공고 보기
    public Page<Recruit> findByAllList(Long idx,Pageable pageable){
        return recruitRepository.findByCorp_CorpIdx(idx,pageable);
    }


    // 공고 삭제 하기
    @Transactional
    public void deleteById(Long recruitIdx){
        applyRepository.deleteByRecruitIdx(recruitIdx); // apply 에서 해당 공고 삭제
        recruitLikeRepository.deleteByRecruitIdx(recruitIdx); // 좋아요 삭제
        recruitRepository.deleteById(recruitIdx); // 해당 공고 상제
    }

}
