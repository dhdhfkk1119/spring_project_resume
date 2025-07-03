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

    private List<CommentResponseDto> replies = new ArrayList<>();

    public CommentResponseDto(Comment comment, Long loginUserId) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.username = comment.getUser().getUsername();
        this.userId = comment.getUser().getMemberIdx();
        this.isOwner = (loginUserId != null && loginUserId.equals(this.userId));
    }

    public void addReply(CommentResponseDto reply) {
        this.replies.add(reply);
    }
}
