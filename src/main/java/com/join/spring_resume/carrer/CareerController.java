package com.join.spring_resume.carrer;

import com.join.spring_resume.resume.Resume;
import com.join.spring_resume.resume.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class CareerController {

    private final CareerService careerService;
    private final ResumeService resumeService;



}
