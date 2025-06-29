package com.join.spring_resume.main;

import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RecruitRepository recruitRepository;

    @GetMapping("/")
    public String index(Model model) {

        List<Recruit> recruitList = recruitRepository.findAll();
        
        model.addAttribute("recruitList",recruitList); // 모든 공고등록 가져오기
        return "index";
    }
}
