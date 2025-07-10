package com.join.spring_resume.Like;

import com.join.spring_resume.board.Board;
import com.join.spring_resume.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    // 좋아요 토글 기능
    @Transactional
    public boolean toggleLike(Member member, Board board) {
        Optional<Like> existing = likeRepository.findByMemberAndBoard(member, board);
        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            return false;
        } else {
            Like like = new Like();
            like.setMember(member);
            like.setBoard(board);
            likeRepository.save(like);
            return true;
        }
    }

    // 좋아요 여부 확인
    public boolean isLiked(Member member, Board board) {
        return likeRepository.findByMemberAndBoard(member, board).isPresent();
    }

    // 좋아요 수 카운트
    public long countLikes(Board board) {
        return likeRepository.countByBoard(board);
    }

    // 좋아요한 게시글 목록 보기
    public List<Board> findLikeBoardsByMember(Member member) {
        return likeRepository.findByMember(member)
                .stream()
                .map(Like::getBoard)
                .toList();
    }
}
