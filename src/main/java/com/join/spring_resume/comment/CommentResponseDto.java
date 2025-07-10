package com.join.spring_resume.comment;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDto {

    private Long id;
    private String comment;
    private String createdAt;
    private String username;
    private Long userId;
    private boolean isOwner;
    private boolean isSecret;
    private boolean canView;

    private List<CommentResponseDto> replies = new ArrayList<>();

    public CommentResponseDto(Comment comment, Long loginUserId) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.username = comment.getUser().getUsername();
        this.userId = comment.getUser().getMemberIdx();
        this.isOwner = (loginUserId != null && loginUserId.equals(this.userId));
        this.isSecret = comment.isSecret();
        // 비밀 댓글 여부에 따른 표시 권한
        this.canView = !isSecret || isOwner;
        if (canView) {
            this.comment = comment.getComment();
        } else {
            this.comment = "🔒 비밀 댓글입니다.";
        }
    }

    public void addReply(CommentResponseDto reply) {
        this.replies.add(reply);
    }
}
