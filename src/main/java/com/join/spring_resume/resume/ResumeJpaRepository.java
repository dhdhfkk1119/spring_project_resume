package com.join.spring_resume.resume;

import com.join.spring_resume.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// <Resume,Long> 제네릭 부분 빼먹어서 수정함
public interface ResumeJpaRepository extends JpaRepository<Resume,Long> {

    //이력서 전체조회
    @Query("SELECT DISTINCT r FROM Resume r LEFT JOIN FETCH r.careerList " +
            "WHERE r.member.memberIdx = :memberIdx ORDER BY r.resumeIdx DESC")
    List<Resume> findAllByMemberIdx(@Param("memberIdx") Long memberIdx);


    //이력서 단건조회
    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.careerList WHERE r.resumeIdx = :resumeIdx")
    Optional<Resume> findByIdWithCareers(@Param("resumeIdx") Long resumeIdx);

    // [추가] 특정 회원의 모든 이력서의 isRep 플래그를 false로 초기화하는 메서드
    @Modifying // 이 쿼리가 DB 상태를 변경함을 알림
    @Query("UPDATE Resume r SET r.isRep = false WHERE r.member.memberIdx = :memberIdx")
    void resetAllIsRepByMemberIdx(@Param("memberIdx") Long memberIdx);

    @Query("SELECT r FROM Resume r WHERE r.member = :member AND r.isRep = true")
    Optional<Resume> findRepresentativeResumeByMember(@Param("member") Member member);

}

