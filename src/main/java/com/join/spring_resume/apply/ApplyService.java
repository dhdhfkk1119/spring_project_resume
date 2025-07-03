package com.join.spring_resume.apply;

import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.resume.Resume;
import com.join.spring_resume.session.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplyService {

    private final ApplyRepository applyRepository;

    @Transactional
    public Apply applySave(ApplyRequest.SaveDTO saveDTO, Recruit recruit , Resume resume){
        return applyRepository.save(saveDTO.toEntity(recruit,resume));
    }

}
