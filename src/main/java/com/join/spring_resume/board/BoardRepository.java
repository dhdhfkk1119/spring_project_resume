package com.join.spring_resume.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {


    // 게시글 기본 정보 + 좋아요 수 + 댓글 수 + 작성자 정보 반환 (페이징 + 키워드 검색 포함)
    @Query("""
        SELECT new com.join.spring_resume.board.BoardListResponseDto(
            b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt,
            COUNT(DISTINCT c.id),
            COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
            m.memberIdx, m.username)
        FROM Board b
        JOIN b.member m
        LEFT JOIN Comment c ON c.board = b
        LEFT JOIN LikeEntity l ON l.board = b AND l.likeYn = true
        WHERE (:keyword IS NULL OR b.boardTitle LIKE CONCAT('%', :keyword, '%') OR b.boardContent LIKE CONCAT('%', :keyword, '%'))
        GROUP BY b.boardIdx, m.memberIdx, m.username, b.createdAt
        ORDER BY b.createdAt DESC
        """)
    Page<BoardListResponseDto> findBoardListWithCounts(@Param("keyword") String keyword, Pageable pageable);

    // 내가 쓴 글 목록 DTO (페이징)
    @Query("""
        SELECT new com.join.spring_resume.board.BoardListResponseDto(
            b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt,
            COUNT(DISTINCT c.id),
            COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
            m.memberIdx, m.username)
        FROM Board b
        JOIN b.member m
        LEFT JOIN Comment c ON c.board = b
        LEFT JOIN LikeEntity l ON l.board = b AND l.likeYn = true
        WHERE m.memberIdx = :memberIdx
        GROUP BY b.boardIdx, m.memberIdx, m.username, b.createdAt
        ORDER BY b.createdAt DESC
        """)
    Page<BoardListResponseDto> findBoardListByMemberIdx(@Param("memberIdx") Long memberIdx, Pageable pageable);

    // 좋아요 누른 게시글 목록 (리스트)
    @Query("""
        SELECT new com.join.spring_resume.board.BoardListResponseDto(
            b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt,
            COUNT(DISTINCT c.id),
            COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
            m.memberIdx, m.username)
        FROM Board b
        JOIN b.member m
        LEFT JOIN Comment c ON c.board = b
        LEFT JOIN LikeEntity l ON l.board = b AND l.likeYn = true
        WHERE EXISTS (
            SELECT 1 FROM LikeEntity lk WHERE lk.board = b AND lk.member.memberIdx = :memberIdx AND lk.likeYn = true
        )
        GROUP BY b.boardIdx, m.memberIdx, m.username, b.createdAt
        ORDER BY b.createdAt DESC
        """)
    List<BoardListResponseDto> findBoardsLikedByMember(@Param("memberIdx") Long memberIdx);

    // 내가 댓글 단 게시글 목록 (리스트)
    @Query("""
        SELECT DISTINCT new com.join.spring_resume.board.BoardListResponseDto(
            b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt,
            COUNT(DISTINCT c.id),
            COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
            m.memberIdx, m.username)
        FROM Comment c
        JOIN c.board b
        JOIN b.member m
        LEFT JOIN LikeEntity l ON l.board = b AND l.likeYn = true
        WHERE c.user.memberIdx = :memberIdx
        GROUP BY b.boardIdx, m.memberIdx, m.username, b.createdAt
        ORDER BY b.createdAt DESC
        """)
    List<BoardListResponseDto> findBoardsCommentedByMember(@Param("memberIdx") Long memberIdx);

    // Member의 memberIdx로 페이징 조회 (엔티티 반환)
    Page<Board> findByMember_MemberIdx(Long memberIdx, Pageable pageable);

    // 키워드 검색 (엔티티 반환)
    @Query("SELECT b FROM Board b WHERE (:keyword IS NULL OR b.boardTitle LIKE CONCAT('%', :keyword, '%') OR b.boardContent LIKE CONCAT('%', :keyword, '%'))")
    Page<Board> searchBoards(@Param("keyword") String keyword, Pageable pageable);



    List<Board> findByMember_MemberIdx(Long memberIdx);


}
