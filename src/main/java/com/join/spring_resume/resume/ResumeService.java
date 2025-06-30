package com.join.spring_resume.resume;

import com.join.spring_resume.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ResumeService {

    private final ResumeJpaRepository resumeJpaRepository;

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
        resume.setMember(member);
        return resumeJpaRepository.save(resume);
    }

    //이력서 수정
    public void updateById(Long resumeIdx, ResumeRequest.UpdateDTO updateDTO, Member member) {

        Resume resume = this.findById(resumeIdx);

        if (!resume.isOwner(member.getMemberIdx())) {
            throw new NullPointerException("본인이 작성한 이력서만 삭제할 수 있습니다");
        }
        resume.setResumeContent(updateDTO.getResumeContent());
    }

    //이력서 삭제
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
