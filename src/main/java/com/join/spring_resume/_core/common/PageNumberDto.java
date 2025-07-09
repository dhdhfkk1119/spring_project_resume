package com.join.spring_resume._core.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageNumberDto {
    private final int number;
    private final int displayNumber;
    private final boolean current;

    public PageNumberDto(int number, int currentPage) {
        this.number = number;
        this.displayNumber = number + 1;
        this.current = number == currentPage;
    }

    @Data
    public static class PageNavigation {
        private final List<PageNumberDto> pageNumbers;
        private final boolean hasPrev;
        private final boolean hasNext;
        private final int prevPage;
        private final int nextPage;
    }


    public static PageNavigation createNavigation(Page<?> page) {
        int totalPages = page.getTotalPages();
        int currentPage = page.getNumber();
        int displayRange = 5;

        int startPage = Math.max(0, currentPage - displayRange / 2);
        int endPage = Math.min(startPage + displayRange - 1, totalPages - 1);
        if (endPage - startPage < displayRange - 1) {
            startPage = Math.max(0, endPage - displayRange + 1);
        }

        List<PageNumberDto> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(new PageNumberDto(i, currentPage));
        }

        boolean hasPrev = currentPage > 0;
        boolean hasNext = currentPage < totalPages - 1;
        int prevPage = hasPrev ? currentPage - 1 : 0;
        int nextPage = hasNext ? currentPage + 1 : totalPages - 1;

        return new PageNavigation(pageNumbers, hasPrev, hasNext, prevPage, nextPage);
    }


}
