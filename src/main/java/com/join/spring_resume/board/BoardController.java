package com.join.spring_resume.board;

import com.join.spring_resume.Like.LikeService;
import com.join.spring_resume._core.common.PageNumberDto;
import com.join.spring_resume._core.errors.exception.Exception401;
import com.join.spring_resume._core.errors.exception.Exception404;
import com.join.spring_resume.comment.Comment;
import com.join.spring_resume.comment.CommentResponseDto;
import com.join.spring_resume.comment.CommentService;
import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import com.join.spring_resume.session.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final LikeService likeService;

    private Member getLoggedInMember(HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        return memberRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new Exception404("유저를 찾을 수 없습니다."));
    }

    // 게시글 리스트
    @GetMapping("/list")
    public String listBoards(@RequestParam(defaultValue = "createdAt") String sort,
                             @RequestParam(defaultValue = "desc") String direction,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             Model model,
                             HttpSession session) {

        keyword = keyword == null ? "" : keyword;
        Sort sorting = direction.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<BoardListResponseDto> boardPage = boardService.getBoardList(keyword, pageable);
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        boardPage.forEach(boardListResponseDto -> {
            if (sessionUser != null && boardListResponseDto.getUsername().equals(sessionUser.getUsername())) {
                boardListResponseDto.setAuthor(true);
            }
        });

        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(boardPage);

        model.addAttribute("boardList", boardPage);
        model.addAttribute("pageNumbers", navigation.getPageNumbers());
        model.addAttribute("hasPrev", navigation.isHasPrev());
        model.addAttribute("hasNext", navigation.isHasNext());
        model.addAttribute("prevPage", navigation.getPrevPage());
        model.addAttribute("nextPage", navigation.getNextPage());

        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isSortCreatedAt", sort.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sort.equals("boardHits"));
        model.addAttribute("isDesc", direction.equals("desc"));
        model.addAttribute("isAsc", direction.equals("asc"));

        return "board/list";
    }

    //  새글 작성 폼
    @GetMapping("/new")
    public String newBoardForm(HttpSession session, Model model) {
        getLoggedInMember(session);
        model.addAttribute("isEdit", false);
        model.addAttribute("boardTitle", "");
        model.addAttribute("boardContent", "");
        model.addAttribute("tags", "");
        return "board/form";
    }

    @PostMapping
    public String createBoard(@ModelAttribute BoardCreateDto dto, HttpSession session) {
        Member member = getLoggedInMember(session);
        boardService.create(dto, member);
        return "redirect:/board/list";
    }

    // 수정 폼
    @GetMapping("/{id}/edit")
    public String editBoardForm(@PathVariable Long id, Model model, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);
        if (!board.isOwner(member.getMemberIdx())) throw new Exception401("수정 권한이 없습니다.");

        model.addAttribute("isEdit", true);
        model.addAttribute("boardIdx", board.getBoardIdx());
        model.addAttribute("boardTitle", board.getBoardTitle());
        model.addAttribute("boardContent", board.getBoardContent());
        model.addAttribute("tags", board.getTags());
        return "board/form";
    }

    @PostMapping("/{id}/edit")
    public String updateBoard(@PathVariable Long id,
                              @ModelAttribute BoardUpdateDto dto,
                              HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);
        if (!board.isOwner(member.getMemberIdx())) throw new Exception401("수정 권한이 없습니다.");
        boardService.update(id, dto);
        return "redirect:/board/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(id);
        if (!board.isOwner(member.getMemberIdx())) throw new Exception401("삭제 권한이 없습니다.");
        boardService.delete(id);
        return "redirect:/board/list";
    }

    // 상세 조회
    @GetMapping("/{id}")
    public String viewBoard(@PathVariable Long id, Model model, HttpSession session) {
        Board board = boardService.findByIdAndIncreaseHits(id);
        if (board.getTags() == null) board.setTags("");

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        Long loginUserId = sessionUser != null ? sessionUser.getId() : null;

        if (sessionUser != null && board.isOwner(sessionUser.getId())) {
            model.addAttribute("isAuthor", true);
        }

        List<Comment> commentEntities = commentService.findCommentsByBoard(id);
        List<CommentResponseDto> commentDtos = commentEntities.stream()
                .map(parent -> {
                    CommentResponseDto dto = new CommentResponseDto(parent, loginUserId);
                    parent.getReplies().forEach(reply -> dto.addReply(new CommentResponseDto(reply, loginUserId)));
                    return dto;
                }).toList();

        model.addAttribute("board", board);
        model.addAttribute("formattedCreatedAt", board.getFormattedCreatedAt());
        model.addAttribute("formattedUpdatedAt", board.getFormattedUpdatedAt());
        model.addAttribute("comments", commentDtos);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("loginUserId", loginUserId);

        boolean isLiked = false;
        long likeCount = likeService.countLikes(board);
        if (sessionUser != null) {
            Member member = memberRepository.findById(sessionUser.getId()).orElseThrow();
            isLiked = likeService.isLiked(member, board);
        }

        model.addAttribute("isLiked", isLiked);
        model.addAttribute("likeCount", likeCount);
        return "board/detail";
    }

    // 댓글 작성
    @PostMapping("/{boardId}/comment")
    public String writeComment(@PathVariable Long boardId,
                               @RequestParam String content,
                               @RequestParam(required = false) Long parentId,
                               HttpSession session) {
        Member member = getLoggedInMember(session);
        commentService.writeComment(boardId, content, member.getMemberIdx(), parentId);
        return "redirect:/board/" + boardId;
    }

    // 좋아요
    @PostMapping("/{boardId}/like")
    public String toggleLike(@PathVariable Long boardId, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(boardId);
        likeService.toggleLike(member, board);
        return "redirect:/board/" + boardId;
    }

    // 내가 쓴 글 목록
    @GetMapping("/my-list")
    public String myList(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "createdAt") String sort,
                         @RequestParam(defaultValue = "desc") String direction,
                         HttpSession session, Model model) {
        Member member = getLoggedInMember(session);
        Sort sorting = direction.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<Board> boardPage = boardService.findByMemberIdx(member.getMemberIdx(), pageable);

        boardPage.forEach(dto -> {
            dto.setAuthor(true);
            dto.setFormattedCreatedAt(String.valueOf(dto.getCreatedAt()));
        });

        model.addAttribute("boardList", boardPage);
        model.addAttribute("sessionUser", session.getAttribute("session"));
        model.addAttribute("myList", true);
        return "board/my-list";
    }

    //  좋아요 누른 게시글
    @GetMapping("/likes")
    public String likedBoard(HttpSession session, Model model) {
        Member member = getLoggedInMember(session);
        List<BoardListResponseDto> likedBoards = boardService.getBoardsLikedByMember(member);

        likedBoards.forEach(board -> {
            board.setAuthor(true);
            String formattedDate = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            board.setFormattedCreatedAt(formattedDate);
        });

        model.addAttribute("boardList", likedBoards);
        model.addAttribute("sessionUser", session.getAttribute("session"));
        model.addAttribute("likedList", true);
        return "board/liked-list";
    }

    // 내가 댓글 단 게시글
    @GetMapping("/comments")
    public String myComments(HttpSession session, Model model) {
        Member member = getLoggedInMember(session);
        List<BoardListResponseDto> boards = boardService.getBoardsCommentedByMember(member);

        boards.forEach(board -> {
            board.setAuthor(true);
            String formattedDate = board.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            board.setFormattedCreatedAt(formattedDate);
        });

        model.addAttribute("boardList", boards);
        model.addAttribute("sessionUser", session.getAttribute("session"));
        model.addAttribute("MyCommentList", true);
        return "board/my-comment-list";
    }

    @GetMapping("/my-boards")
    public String myBoards(HttpSession session, Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        Long memberIdx = sessionUser.getId();

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BoardListResponseDto> boardPage = boardService.findBoardListByMemberIdx(memberIdx, pageable);

        model.addAttribute("boardList", boardPage);
        return "board/my-boards";
    }


}
