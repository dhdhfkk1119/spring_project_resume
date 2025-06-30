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

    //이력서 저장
    @Transactional
    public Resume save(ResumeRequest.saveDTO saveDTO, Member sessionMember) {
        Resume resume = saveDTO.toEntity();
        resumeJpaRepository.save(resume);
        return resume;
    }

    //이력서 목록조회
    public List<Resume> findAll() {
        List<Resume> resumeList = resumeJpaRepository.findAll();
        return resumeList;
    }

    //이력서 단건조회
    public Resume findById(Long resumeIdx) {
        Resume resume = resumeJpaRepository.findById(resumeIdx).orElseThrow(() -> {
            return new NullPointerException("이력서를 찾을 수 없습니다");
        });
        return resume;
    }

    //게시글 수정
    public Resume updateById(Long resumeIdx, ResumeRequest.UpdateDTO updateDTO, Member member) {

        Resume resume = resumeJpaRepository.findById(resumeIdx).orElseThrow(() -> {
            return new NullPointerException("이력서를 찾을 수 없습니다");
        });
        if (!resume.isOwner(member.getMemberIdx())) {
            throw new NullPointerException("본인이 작성한 이력서만 수정할 수 있습니다");
        }
        resume.setResumeContent(updateDTO.getResumeContent());
        return resume;
    }

    //게시글 삭제
    public void deleteById(Long resumeIdx, Member member) {
        Resume resume = resumeJpaRepository.findById(resumeIdx).orElseThrow(() -> {
            return new NullPointerException("이력서를 찾을 수 없습니다");
        });
        if (!resume.isOwner(member.getMemberIdx())) {
            throw new NullPointerException("본인이 작성한 이력서만 삭제할 수 있습니다");
        }
        resumeJpaRepository.deleteById(resumeIdx);
    }

    //이력서 권한체크 (수정화면 요청 확인용)
    public void checkResumeOwner(Long resumeIdx, Long memberIdx) {
        Resume resume = findById(resumeIdx);
        if (!resume.isOwner(memberIdx)) {
            throw new NullPointerException("본인이 작성한 이력서만 수정할 수 있습니다");
        }
    }

    //내 이력서만
    public List<Resume> findMyResumes(Long memberIdx) {
        return resumeJpaRepository.findAllByMemberIdx(memberIdx);
    }
}//
