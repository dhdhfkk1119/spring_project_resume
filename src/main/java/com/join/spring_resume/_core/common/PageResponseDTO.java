package com.join.spring_resume._core.common;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean last;

    public static <T, E> PageResponseDTO<T> from(Page<E> page, Function<E, T> converter) {
        List<T> dtoList = page.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                dtoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast()
        );
    }
}
