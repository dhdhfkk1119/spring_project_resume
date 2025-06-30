package com.join.spring_resume.board;

import com.join.spring_resume.member.Member;
import com.join.spring_resume.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
public class BoardServiceTest {
    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    private Board testBoard;
    private Board savedBoard;
    private Long boardId;

    @BeforeEach
    public void setup() {
        // 테스트용 게시글 저장
        Member member = memberRepository.findById(1L)
                .orElseThrow();

        Board board = new Board();
        board.setMember(member);
        board.setBoardTitle("테스트 제목");
        board.setBoardContent("테스트 내용");
        board.setBoardHits(0);
        board.setTags("test");

        savedBoard = boardRepository.save(board);
    }

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    public void testFindById() {

        Board board = boardService.findById(testBoard.getBoardIdx());
        assertThat(board.getBoardTitle()).isEqualTo("테스트 제목");
        assertThat(board.getBoardContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("게시글 단건 조회 실패 - 존재하지 않는 ID")
    void testViewBoard_NotFound() throws Exception {
        // given
        given(boardService.findById(999L))
                .willThrow(new IllegalArgumentException("게시글이 존재하지 않습니다"));

        // when & then
        mockMvc.perform(get("/board/999"))
                .andExpect(status().isOk()) // 예외 발생 시 404 등으로 처리하려면 ControllerAdvice 필요
                .andExpect(model().hasNoErrors());
    }

    @Test
    @DisplayName("없는 게시글 조회 시 예외 발생 테스트")
    void testFindById_Exception() {
        assertThrows(IllegalArgumentException.class, () -> {
            boardService.findById(9999L);
        });
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void testUpdate() {
        Board updatedData = new Board();
        updatedData.setBoardTitle("수정된 제목");
        updatedData.setBoardContent("수정된 내용");
        updatedData.setTags("새태그");

        Board updated = boardService.update(savedBoard.getBoardIdx(), updatedData);

        assertThat(updated.getBoardTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getBoardContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void testDelete() {
        boardService.delete(savedBoard.getBoardIdx());

        boolean exists = boardRepository.existsById(savedBoard.getBoardIdx());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("조회수 증가 테스트")
    void testIncreaseHits() {
        boardService.increaseHits(savedBoard.getBoardIdx());

        Board updated = boardRepository.findById(savedBoard.getBoardIdx()).get();
        assertThat(updated.getBoardHits()).isEqualTo(1);
    }

    @Test
    @DisplayName("게시글 상세 페이지 접근 테스트")
    public void testViewBoard() throws Exception {
        mockMvc.perform(get("/board/" + boardId))
                .andExpect(status().isOk())
                .andExpect(view().name("board/detail"))
                .andExpect(model().attributeExists("board"))
                .andExpect(model().attribute("board", org.hamcrest.Matchers.hasProperty("boardTitle")));
    }
}
