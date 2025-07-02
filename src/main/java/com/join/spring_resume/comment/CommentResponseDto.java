package com.join.spring_resume.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommentResponseDto {

    private Long id;
    private String comment;
    private String createdAt;
    private String username;

    private List<CommentResponseDto> replies = new ArrayList<>();

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.username = comment.getUser().getUsername();
    }

    public void addReply(CommentResponseDto reply) {
        this.replies.add(reply);
    }
}
