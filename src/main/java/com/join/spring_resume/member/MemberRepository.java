package com.join.spring_resume.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsByMemberId(String memberId);
    Optional<Member> findByMemberId(String memberId);
    Optional<Member> findByMemberIdx(Long idx);
}
