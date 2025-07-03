package com.join.spring_resume.apply;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplyRepository extends JpaRepository<Apply,Long> {
//    Apply findByApplyIdx(Long idx);
//    boolean existsByResume_Member_IdxAndRecruit_Corp_Idx(Long memberIdx, Long corpIdx);

}
