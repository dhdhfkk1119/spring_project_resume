package com.join.spring_resume.recruit;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit,Long> {
    Optional<Recruit> findByRecruitIdx(Long idx);

    // 현재 로그인한 기업이 등록한 공고 정보 보기
    @Query("select r from Recruit r join fetch r.corp c where r.corp.id = :corpIdx order by r.id desc")
    Page<Recruit> findByCorp_CorpIdx(Long corpIdx,Pageable pageable); // 모 기업이 등록한 모든 공고 보기

    // 모든 공고 정보 보여주기
    @Query("select r from Recruit r join fetch r.corp c order by r.id desc")
    Page<Recruit> findAll(Pageable pageable);

    // 기업 ID로 공고 수 카운트
    @Query("select count(r) from Recruit r where r.corp.corpIdx = :corpIdx")
    long countByCorpIdx(@Param("corpIdx") Long corpIdx);

}
