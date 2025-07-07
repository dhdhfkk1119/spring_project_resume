package com.join.spring_resume.board;

import lombok.Data;

@Data
public class PageNumberDto {
    private final int number;
    private final int displayNumber;
    private final boolean current;  // 필드명 소문자로 변경

    public PageNumberDto(int number, int currentPage) {
        this.number = number;
        this.displayNumber = number + 1;
        this.current = number == currentPage;
    }

}
