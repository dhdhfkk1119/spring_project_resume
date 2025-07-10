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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/list")
    public String listBoards(@RequestParam(defaultValue = "createdAt") String sort,
                             @RequestParam(defaultValue = "desc") String direction,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             Model model,
                             HttpSession session) {

        if (keyword == null) {
            keyword = "";
        }
        model.addAttribute("keyword", keyword);

        Sort sorting = direction.equalsIgnoreCase("asc") ?
                Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<BoardListResponseDto> boardPage = boardService.getBoardList(keyword, pageable);
        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        boardPage.forEach(dto -> {
            if (sessionUser != null && dto.getUsername().equals(sessionUser.getUsername())) {
                dto.setAuthor(true);
            }
        });

        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(boardPage);

        model.addAttribute("boardList", boardPage);
        model.addAttribute("navigation", navigation);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("isSortCreatedAt", sort.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sort.equals("boardHits"));
        model.addAttribute("isDesc", direction.equals("desc"));
        model.addAttribute("isAsc", direction.equals("asc"));
        model.addAttribute("keyword", keyword);

        return "board/list";
    }

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
    public String updateBoard(@PathVariable Long id, @ModelAttribute BoardUpdateDto dto, HttpSession session) {
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


        int commentCount = commentDtos.stream()
                .mapToInt(dto -> 1 + dto.getReplies().size())
                .sum();

        model.addAttribute("board", board);
        model.addAttribute("formattedCreatedAt", board.getFormattedCreatedAt());
        model.addAttribute("formattedUpdatedAt", board.getFormattedUpdatedAt());
        model.addAttribute("comments", commentDtos);
        model.addAttribute("commentCount", commentCount);
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

    @PostMapping("/{boardId}/comment")
    public String writeComment(@PathVariable Long boardId,
                               @RequestParam String content,
                               @RequestParam(required = false) Long parentId,
                               HttpSession session) {
        Member member = getLoggedInMember(session);
        commentService.writeComment(boardId, content, member.getMemberIdx(), parentId);
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/{boardId}/like")
    public String toggleLike(@PathVariable Long boardId, HttpSession session) {
        Member member = getLoggedInMember(session);
        Board board = boardService.findById(boardId);
        likeService.toggleLike(member, board);
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/my-list")
    public String myBoards(HttpSession session, Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(defaultValue = "createdAt") String sort,
                           @RequestParam(defaultValue = "desc") String direction) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");

        Long memberIdx = sessionUser.getId();
        if (keyword == null) {
            keyword = "";
        }
        model.addAttribute("keyword", keyword);

        Sort sorting = direction.equalsIgnoreCase("asc") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<BoardListResponseDto> boardPage = boardService.searchMyBoards(memberIdx, keyword, pageable);

        boardPage.forEach(dto -> {
            if (dto.getMemberIdx().equals(memberIdx)) {
                dto.setAuthor(true);
            }
        });

        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(boardPage);

        model.addAttribute("boardList", boardPage);
        model.addAttribute("navigation", navigation);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("isSortCreatedAt", sort.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sort.equals("boardHits"));
        model.addAttribute("isDesc", direction.equals("desc"));
        model.addAttribute("isAsc", direction.equals("asc"));

        return "board/my-list";
    }

    @GetMapping("/likes")
    public String likedBoard(HttpSession session, Model model) {
        Member member = getLoggedInMember(session);
        List<BoardListResponseDto> likedBoards = boardService.findBoardsLikedByMember(member);

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
    public String myBoardsPaging(HttpSession session, Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "createdAt") String sort,
                                 @RequestParam(defaultValue = "desc") String direction) {

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");
        if (sessionUser == null) throw new Exception401("로그인이 필요합니다.");
        Long memberIdx = sessionUser.getId();

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<BoardListResponseDto> boardPage = boardService.getMyBoards(memberIdx, pageable);

        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(boardPage);

        model.addAttribute("boardList", boardPage);
        model.addAttribute("navigation", navigation);
        model.addAttribute("sessionUser", sessionUser);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("isSortCreatedAt", sort.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sort.equals("boardHits"));
        model.addAttribute("isDesc", direction.equals("desc"));
        model.addAttribute("isAsc", direction.equals("asc"));

        return "board/my-boards";
    }

    @GetMapping("/boards")
    public String boardList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model,
            HttpSession session
    ) {
        if (keyword == null) {
            keyword = "";
        }
        model.addAttribute("keyword", keyword);

        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, 10, sort);

        Page<BoardListResponseDto> boardPage = boardService.getBoardList(keyword, pageable);

        PageNumberDto.PageNavigation navigation = PageNumberDto.createNavigation(boardPage);

        SessionUser sessionUser = (SessionUser) session.getAttribute("session");

        model.addAttribute("boardList", boardPage);
        model.addAttribute("navigation", navigation);
        model.addAttribute("sort", sortBy);  // sortBy -> sort 통일
        model.addAttribute("direction", direction);
        model.addAttribute("isSortCreatedAt", sortBy.equals("createdAt"));
        model.addAttribute("isSortBoardHits", sortBy.equals("boardHits"));
        model.addAttribute("isDesc", direction.equals("desc"));
        model.addAttribute("isAsc", direction.equals("asc"));
        model.addAttribute("sessionUser", sessionUser);

        return "board/list";
    }
}

