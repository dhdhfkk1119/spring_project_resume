    package com.join.spring_resume.resume;

    import com.join.spring_resume.member.Member;
    import com.join.spring_resume.member.MemberRepository;
    import com.join.spring_resume.session.SessionUser;
    import jakarta.servlet.http.HttpSession;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;

    import java.util.List;

    @RequiredArgsConstructor
    @Controller
    public class ResumeController {

        private final ResumeService resumeService;
        private final HttpSession session;
        private final MemberRepository memberRepository;
        /**
         * 이력서 관리 (목록보기)
         */
        @GetMapping("/resumes")
        public String list(Model model) {
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");
            if (sessionUser == null) return "redirect:/login-form";
            System.out.println("로그인된 사용자 ID: " + sessionUser.getId());
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }

            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            //세션에서 가져온 memberIdx 회원이력서 전체조회
            List<Resume> resumeList = resumeService.findMyResumes(member.getMemberIdx());

            model.addAttribute("resumes", resumeList);
            return "resume/list";
        }

        /**
         * 이력서 수정 화면 요청
         * 2.인증검사
         * 3.게시물 조회 = 서비스 위임
         */
        @GetMapping("/resume/{id}/update-form")
        public String updateForm(@PathVariable(name = "id") Long resumeIdx, Model model) {
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");
            if (sessionUser == null) return "redirect:/login-form";
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            resumeService.checkResumeOwner(resumeIdx, member.getMemberIdx());
            model.addAttribute("resume", resumeService.findById(resumeIdx));
            return "resume/update-form";
        }


        /**
         * 수정 액션
         * 1.인증검사
         * 2.유효성검사
         * 3.수정 = 서비스 위임
         * 4.리다이렉트
         */
        @PostMapping("/resume/{id}/update-form")
        public String update(@PathVariable(name = "id") Long resumeIdx, ResumeRequest.UpdateDTO reqDTO) {
            reqDTO.validate();
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");
            if (sessionUser == null) return "redirect:/login-form";
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            resumeService.updateById(resumeIdx, reqDTO, member);
            return "redirect:/resume/" + resumeIdx;
        }

        /**
         * 이력서 삭제
         * 1.인증검사
         * 2.세션 로그인 정보 확인
         * 3.삭제 = 서비스위임
         * 4.리다이렉트
         */
        @PostMapping("/resume/{id}/delete")
        public String delete(@PathVariable(name = "id") Long resumeIdx) {
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");
            if (sessionUser == null) return "redirect:/login-form";
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            resumeService.deleteById(resumeIdx, member);
            return "redirect:/resume";
        }

        /**
         * 이력서 작성창 요청
         * 1.세션 로그인 정보 확인
         */

        @GetMapping("/resume/save-form")
        public String saveForm() {
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");
            if (sessionUser == null) return "redirect:/login-form";
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            return "resume/save-form";
        }

        /**
         * 이력서 저장 액션
         * 1.인증검사
         * 2.유효성검사
         * 3.저장 = 서비스위임
         */

        @PostMapping("/resume/save")
        public String save(ResumeRequest.saveDTO reqDTO) {
            reqDTO.validate();
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");

            if (sessionUser == null) return "redirect:/login-form";
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            Resume resume = resumeService.save(reqDTO, member);
            return "redirect:/resume/" + resume.getResumeIdx();
        }

        /**
         * 이력서 상세보기 화면 요청
         * 1.인증검사
         * 2.유효성검사
         * 3.저장 = 서비스위임
         */
        @GetMapping("/resume/{id}")
        public String detail(@PathVariable(name = "id") Long resumeIdx, Model model) {
            SessionUser sessionUser = (SessionUser) session.getAttribute("session");
            if (sessionUser == null) return "redirect:/login-form";
            if(sessionUser.getRole() != "MEMBER"){
                System.out.println("일반 회원만 작성 가능합니다");
                return "redirect:/";
            }
            Member member = memberRepository.findById(sessionUser.getId())
                    .orElseThrow(() -> new RuntimeException("해당 회원을 찾을수 없습니다"));

            model.addAttribute("resume", resumeService.findById(resumeIdx));
            return "resume/detail";
        }


    }//
