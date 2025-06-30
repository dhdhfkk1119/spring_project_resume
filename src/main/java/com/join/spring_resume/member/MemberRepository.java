package com.join.spring_resume.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByMemberIdx(Long userId); // 아이디 찾기
    Optional<Member> findByMemberIdAndPassword(String memberId , String password);
}
