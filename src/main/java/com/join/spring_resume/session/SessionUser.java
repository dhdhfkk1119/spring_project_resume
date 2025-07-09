package com.join.spring_resume.session;

import com.join.spring_resume.corp.Corp;
import com.join.spring_resume.corp.CorpRequest;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionUser {
    private Long id;
    private String userId;
    private String username;
    private String role;
    private String images;

    public static SessionUser fromMember(Member member) {
        return new SessionUser(member.getMemberIdx() ,member.getMemberId(), member.getUsername(), "MEMBER",member.getMemberImage());
    }

    public static SessionUser fromCorp(Corp corp) {
        return new SessionUser(corp.getCorpIdx(), corp.getCorpId(),corp.getCorpName() , "CORP",corp.getCorpImage());
    }
}
