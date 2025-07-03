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

    //이력서와 경력 함께 조회
    public Resume findByIdWithCareers(Long resumeIdx) {
        // 이전에 추가했던 JOIN FETCH 쿼리를 사용합니다.
        return resumeJpaRepository.findByIdWithCareers(resumeIdx)
                .orElseThrow(() -> new IllegalArgumentException("해당 이력서를 찾을 수 없습니다. id: " + resumeIdx));
    }

    //이력서 저장
    @Transactional
    public Resume save(ResumeRequest.saveDTO saveDTO, Member member) {
        Resume resume = saveDTO.toEntity(member);
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
        //resume.setMember(member);
        return savedResume;
    }

    //이력서 수정
    @Transactional
    public void updateById(Long resumeIdx, ResumeRequest.UpdateDTO updateDTO, Member sessionMember) {

        // 1. 이력서 조회 및 소유권 확인
        Resume resume = resumeJpaRepository.findById(resumeIdx)
                .orElseThrow(() -> new RuntimeException("해당 이력서를 찾을 수 없습니다. id=" + resumeIdx));

        if (!resume.isOwner(sessionMember.getMemberIdx())) {
            throw new RuntimeException("이력서를 수정할 권한이 없습니다.");
        }

        // 2. 이력서 기본 정보 수정 (JPA의 더티 체킹으로 자동 UPDATE)
        resume.setResumeTitle(updateDTO.getResumeTitle());
        resume.setResumeContent(updateDTO.getResumeContent());

        // 3. 대표 이력서 설정 (체크박스 확인)
        if (Boolean.TRUE.equals(updateDTO.getIsRep())) {
            resumeJpaRepository.resetAllIsRepByMemberIdx(sessionMember.getMemberIdx());
            resume.setIsRep(true);
        } else {
            resume.setIsRep(false);
        }

        // 3. 새로 추가된 경력만 저장
        if (updateDTO.getCareers() != null) {
            for (CareerRequest.UpdateDTO careerDTO : updateDTO.getCareers()) {

                //비어있는 경력 데이터는 저장하지 않고 건너뛰는 방어 코드 추가
                if (careerDTO.getCorpName() == null || careerDTO.getCorpName().trim().isEmpty()) {
                    continue;
                }

                //유효성 검사
                careerDTO.validate();

                Career newCareer = Career.builder()
                        .resume(resume)
                        .corpName(careerDTO.getCorpName())
                        .position(careerDTO.getPosition())
                        .careerContent(careerDTO.getCareerContent())
                        .startAt(careerDTO.getStartAt())
                        .endAt(careerDTO.getEndAt())
                        .build();
                careerJpaRepository.save(newCareer);
            }
        }

        // 4. 삭제 경력사항 제거 - JS 'deletedCareerIds' 호출
        if (updateDTO.getDeletedCareerIds() != null && !updateDTO.getDeletedCareerIds().isEmpty()) {
            careerJpaRepository.deleteAllById(updateDTO.getDeletedCareerIds()); // DELETE 실행
        }
    }

    //이력서 삭제
    @Transactional
    public void deleteById(Long resumeIdx, Member member) {
        Resume resume = this.findByIdWithCareers(resumeIdx);

        if (!resume.isOwner(member.getMemberIdx())) {
            throw new NullPointerException("본인이 작성한 이력서만 삭제할 수 있습니다");
        }
        resumeJpaRepository.delete(resume);
    }
    
    // 대표이력서 찾기
    public Resume findIdMyResumes(Member member) {
        return resumeJpaRepository.findRepresentativeResumeByMember(member)
                .orElseThrow(() -> new IllegalStateException("대표 이력서가 존재하지 않습니다."));
    }
}//
