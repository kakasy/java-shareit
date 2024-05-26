package ru.practicum.shareit.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaginationTest {
    private static final Sort CREATED_DESC = Sort.by(Sort.Direction.DESC, "created");

    @Test
    void withSort_whenValidFromAndSize_thenReturnPageRequest() {

        Integer from = 0;
        Integer size = 10;

        Pageable expectedPageRequest = PageRequest.of(from / size, size, CREATED_DESC);

        Pageable actualPageRequest = Pagination.withSort(from, size, CREATED_DESC);

        assertEquals(expectedPageRequest, actualPageRequest);
    }

    @Test
    void withoutSort_whenValidFromAndSize_thenReturnPageRequest() {

        Integer from = 0;
        Integer size = 10;

        Pageable expectedPageRequest = PageRequest.of(from / size, size);

        Pageable actualPageRequest = Pagination.withoutSort(from, size);

        assertEquals(expectedPageRequest, actualPageRequest);
    }
}
