package com.join.spring_resume.recruit;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit,Long> {
    Optional<Recruit> findByRecruitIdx(Long idx);
    List<Recruit> findByCorp_CorpIdx(Long corpIdx); // 모 기업이 등록한 모든 공고 보기
}
