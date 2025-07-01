package com.join.spring_resume.main;

import com.join.spring_resume.recruit.Recruit;
import com.join.spring_resume.recruit.RecruitRepository;
import com.join.spring_resume.recruit.RecruitResponseDTO;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final RecruitRepository recruitRepository;

    @GetMapping("/")
    public String index(Model model, HttpSession httpSession) {

        SessionUser sessionUser = (SessionUser) httpSession.getAttribute("session");

        List<Recruit> recruitList = recruitRepository.findAll();
        List<RecruitResponseDTO> responseDTO = recruitList.stream()
                        .map(RecruitResponseDTO::new)
                        .collect(Collectors.toList());


        model.addAttribute("recruitList",responseDTO); // 모든 공고등록 가져오기
        model.addAttribute("sessionUser",sessionUser);

        return "index";
    }

    // 마이페이지
    @GetMapping("/member/mypage")
    public String memberMyPage(){
        return "mypage/member-page";
    }

    // 마이페이지
    @GetMapping("/recruit/mypage")
    public String corpMyPage(){
        return "mypage/corp-page";
    }
}
