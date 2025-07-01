package com.join.spring_resume.resume;

import com.join.spring_resume.carrer.Career;
import com.join.spring_resume.carrer.CareerJpaRepository;
import com.join.spring_resume.carrer.CareerRequest;
import com.join.spring_resume.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ResumeService {

    private final ResumeJpaRepository resumeJpaRepository;
    private final CareerJpaRepository careerJpaRepository;

    //관리자용 전체 이력서 조회
    public List<Resume> findAll() {
        return resumeJpaRepository.findAll();
    }

    //회원용 전체 이력서 조회
    public List<Resume> findMyResumes(Long memberIdx) {
        return resumeJpaRepository.findAllByMemberIdx(memberIdx);
    }

    //이력서 idx 조회
    public Resume findById(Long resumeIdx) {
        return resumeJpaRepository.findByResumeIdx(resumeIdx).orElseThrow(() -> {
            return new NullPointerException("이력서를 찾을 수 없습니다. ID: "+ resumeIdx);
        });
    }

    //이력서 저장
    @Transactional
    public Resume save(ResumeRequest.saveDTO saveDTO, Member member) {
        Resume resume = saveDTO.toEntity();
        Resume savedResume = resumeJpaRepository.save(resume);

        List<CareerRequest.SaveDTO> cSaveDTOS = saveDTO.getCareers();
        if(cSaveDTOS != null && !cSaveDTOS.isEmpty()){
            // 각 Career DTO를 Career 엔티티로 변환
            List<Career> careers = cSaveDTOS.stream()
                    .map(careerDTO -> {
                        careerDTO.validate(); // 유효성 검사
                        // 방금 저장한 이력서(savedResume)와 연관관계를 맺어줌
                        return careerDTO.toEntity(savedResume);
                    })
                    .collect(Collectors.toList());

            // 변환된 Career 엔티티 리스트를 한 번에 저장
            careerJpaRepository.saveAll(careers);
        }
        resume.setMember(member);
        return savedResume;
    }

    //이력서 수정
    @Transactional
    public void updateById(Long resumeIdx, ResumeRequest.UpdateDTO updateDTO, Member member) {

        Resume resume = this.findById(resumeIdx);

        if (!resume.isOwner(member.getMemberIdx())) {
            throw new NullPointerException("본인이 작성한 이력서만 삭제할 수 있습니다");
        }
        resume.setResumeContent(updateDTO.getResumeContent());
    }

    //이력서 삭제
    @Transactional
    public void deleteById(Long resumeIdx, Member member) {
        Resume resume = this.findById(resumeIdx);

        if (!resume.isOwner(member.getMemberIdx())) {
            throw new NullPointerException("본인이 작성한 이력서만 삭제할 수 있습니다");
        }
        resumeJpaRepository.delete(resume);
    }

    //이력서 권한체크 (수정화면 요청 확인용)
    public void checkResumeOwner(Long resumeIdx, Long memberIdx) {
        Resume resume = this.findById(resumeIdx);
        if (!resume.isOwner(memberIdx)) {
            throw new NullPointerException("본인이 작성한 이력서만 수정할 수 있습니다");
        }
    }


}//
