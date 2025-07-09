package com.join.spring_resume.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {


    // 2. 내가 쓴 게시글 목록 조회
    @Query("""
                SELECT new com.join.spring_resume.board.BoardListResponseDto(
                    b.boardIdx,
                    b.boardTitle,
                    b.boardContent,
                    b.boardHits,
                    b.createdAt,
                    COUNT(DISTINCT c.id),
                    COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
                    m.memberIdx,
                    m.username,
                    false
                )
                FROM Board b
                JOIN b.member m
                LEFT JOIN b.comments c
                LEFT JOIN b.likes l WITH l.likeYn = true
                WHERE m.memberIdx = :memberIdx
                GROUP BY b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt, m.memberIdx, m.username
                ORDER BY b.createdAt DESC
            """)
    Page<BoardListResponseDto> findBoardListByMemberIdx(@Param("memberIdx") Long memberIdx, Pageable pageable);


    // 3. 좋아요 누른 게시글 목록
    @Query("""
                SELECT new com.join.spring_resume.board.BoardListResponseDto(
                    b.boardIdx,
                    b.boardTitle,
                    b.boardContent,
                    b.boardHits,
                    b.createdAt,
                    COUNT(DISTINCT c.id),
                    COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
                    m.memberIdx,
                    m.username,
                    false
                )
                FROM Board b
                JOIN b.member m
                LEFT JOIN b.comments c
                LEFT JOIN b.likes l WITH l.likeYn = true
                WHERE EXISTS (
                    SELECT 1 FROM LikeEntity lk
                    WHERE lk.board = b AND lk.member.memberIdx = :memberIdx AND lk.likeYn = true
                )
                GROUP BY b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt, m.memberIdx, m.username
                ORDER BY b.createdAt DESC
            """)
    List<BoardListResponseDto> findBoardsLikedByMember(@Param("memberIdx") Long memberIdx);


    // 4. 내가 댓글 단 게시글 목록
    @Query("""
                SELECT DISTINCT new com.join.spring_resume.board.BoardListResponseDto(
                    b.boardIdx,
                    b.boardTitle,
                    b.boardContent,
                    b.boardHits,
                    b.createdAt,
                    COUNT(DISTINCT c2.id),
                    COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
                    m.memberIdx,
                    m.username,
                    false
                )
                FROM Comment c
                JOIN c.board b
                JOIN b.member m
                LEFT JOIN b.comments c2
                LEFT JOIN b.likes l WITH l.likeYn = true
                WHERE c.user.memberIdx = :memberIdx
                GROUP BY b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt, m.memberIdx, m.username
                ORDER BY b.createdAt DESC
            """)
    List<BoardListResponseDto> findBoardsCommentedByMember(@Param("memberIdx") Long memberIdx);


    // 5. 내가 쓴 게시글 목록 + 키워드 검색
    @Query("""
       SELECT new com.join.spring_resume.board.BoardListResponseDto(
           b.boardIdx,
           b.boardTitle,
           b.boardContent,
           b.boardHits,
           b.createdAt,
           COUNT(DISTINCT c.id),
           COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
           m.memberIdx,
           m.username,
           false
       )
       FROM Board b
       JOIN b.member m
       LEFT JOIN b.comments c
       LEFT JOIN b.likes l WITH l.likeYn = true
       WHERE m.memberIdx = :memberIdx
         AND (:keyword IS NULL OR b.boardTitle LIKE CONCAT('%', :keyword, '%') OR b.boardContent LIKE CONCAT('%', :keyword, '%'))
       GROUP BY b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt, m.memberIdx, m.username
   """)
    Page<BoardListResponseDto> searchMyBoardsByKeyword(
            @Param("memberIdx") Long memberIdx,
            @Param("keyword") String keyword,
            Pageable pageable
    );



    // 6. 기본 엔티티 반환용 (옵션)
    Page<Board> findByMember_MemberIdx(Long memberIdx, Pageable pageable);

    @Query("""
                SELECT b
                FROM Board b
                WHERE (:keyword IS NULL OR b.boardTitle LIKE CONCAT('%', :keyword, '%') OR b.boardContent LIKE CONCAT('%', :keyword, '%'))
            """)
    Page<Board> searchBoards(@Param("keyword") String keyword, Pageable pageable);

    List<Board> findByMember_MemberIdx(Long memberIdx);

    // 7. 전체 게시글 목록 DTO (댓글/좋아요 수 제외)
    @Query("""
                SELECT new com.join.spring_resume.board.BoardListResponseDto(
                    b.boardIdx,
                    b.boardTitle,
                    b.boardContent,
                    b.boardHits,
                    b.createdAt,
                    0L, 0L,
                    m.memberIdx,
                    m.username,
                    false
                )
                FROM Board b
                JOIN b.member m
                ORDER BY b.createdAt DESC
            """)
    Page<BoardListResponseDto> findAllBoards(Pageable pageable);

    // 8. 전체 게시글 목록 + 키워드 검색 DTO (댓글/좋아요 수 제외)
    @Query("""
                SELECT new com.join.spring_resume.board.BoardListResponseDto(
                    b.boardIdx,
                    b.boardTitle,
                    b.boardContent,
                    b.boardHits,
                    b.createdAt,
                    COUNT(DISTINCT c.id),
                    COALESCE(SUM(CASE WHEN l.likeYn = true THEN 1 ELSE 0 END), 0),
                    m.memberIdx,
                    m.username,
                    false
                )
                FROM Board b
                JOIN b.member m
                LEFT JOIN b.comments c
                LEFT JOIN b.likes l ON l.likeYn = true
                WHERE (:keyword IS NULL OR b.boardTitle LIKE %:keyword% OR b.boardContent LIKE %:keyword%)
                GROUP BY b.boardIdx, b.boardTitle, b.boardContent, b.boardHits, b.createdAt, m.memberIdx, m.username
            """)
    Page<BoardListResponseDto> findBoardListWithCounts(@Param("keyword") String keyword, Pageable pageable);


    @Query("""
                SELECT new com.join.spring_resume.board.BoardListResponseDto(
                    b.boardIdx,
                    b.boardTitle,
                    b.boardContent,
                    b.boardHits,
                    b.createdAt,
                    0L, 0L,
                    m.memberIdx,
                    m.username,
                    false
                )
                FROM Board b
                JOIN b.member m
                WHERE b.boardTitle LIKE %:keyword% OR b.boardContent LIKE %:keyword%
            """)
    Page<BoardListResponseDto> searchBoardsByKeyword(@Param("keyword") String keyword, Pageable pageable);


}
