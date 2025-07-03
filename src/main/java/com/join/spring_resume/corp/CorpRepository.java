package com.join.spring_resume.corp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpRepository extends JpaRepository<Corp,Long> {
    Optional<Corp> findByCorpIdAndPassword(String corpId, String password);

    Optional<Corp> findByCorpId(String corpId);

    boolean existsByCorpId(String corpId); // 아이디 중복 검사
}
