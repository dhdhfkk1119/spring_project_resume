package com.join.spring_resume.career;

import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume.resume.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CareerService {

    private final CareerJpaRepository careerJpaRepository;

    //경력 저장
    @Transactional
    public void save(CareerRequest.SaveDTO saveDTO, Resume resume) {

        Career career = saveDTO.toEntity(resume);
        careerJpaRepository.save(career);
    }

    //경력 수정
    @Transactional
    public void update(Long careerIdx, CareerRequest.UpdateDTO updateDTO) {
        // updateDTO.validate();
        Career career = careerJpaRepository.findById(careerIdx)
                .orElseThrow(() -> new IllegalArgumentException());
        career.setCorpName(updateDTO.getCorpName());
        career.setPosition(updateDTO.getPosition());
        career.setCareerContent(updateDTO.getCareerContent());
        career.setStartAt(updateDTO.getStartAt());
        career.setEndAt(updateDTO.getEndAt());
    }

    //경력삭제
    @Transactional
    public void deleteCareer(Long careerIdx) {
        Career career = careerJpaRepository.findById(careerIdx)
                .orElseThrow(() -> new Exception404("해당 경력을 찾을 수 없습니다. id:" + careerIdx));
        careerJpaRepository.delete(career);
    }
}
