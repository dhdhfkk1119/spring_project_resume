package com.join.spring_resume.corp;

import com.join.spring_resume._core.errors.exception.Exception400;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorpService {

    private final CorpRepository corpRepository;

    public Corp save(CorpRequest.saveDTO saveDTO){

        return corpRepository.save(saveDTO.toEntity());
    }

    public Corp login(CorpRequest.LoginDTO loginDTO){
        Corp corp = corpRepository.findByCorpId(loginDTO.getCorpId())
                .orElseThrow(() -> new Exception400("아이디 또는 비밀번호가 틀립니다"));

        if (!corp.getPassword().equals(loginDTO.getPassword())) {
            throw new Exception400("아이디 또는 비밀번호가 틀립니다");
        }

        return corp;
    }

    public Corp findById(Long id) {
        return corpRepository.findById(id)
                .orElseThrow(() -> new Exception400("기업을 찾을 수 없습니다."));
    }

    public boolean existsByCorpId(String corpId) {
        return corpRepository.existsByCorpId(corpId);
    }
}
