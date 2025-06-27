package com.join.spring_resume.corp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorpService {

    private final CorpRepository corpRepository;

    public Corp save(CorpRequest.saveDTO saveDTO){
        System.out.println("기업 회원 가입" + saveDTO.toEntity());

        return corpRepository.save(saveDTO.toEntity());
    }

    public Corp login(CorpRequest.LoginDTO loginDTO){
        return corpRepository.findByCorpIdAndPassword(loginDTO.getCorpId(), loginDTO.getPassword())
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않습니다"));
    }
}
