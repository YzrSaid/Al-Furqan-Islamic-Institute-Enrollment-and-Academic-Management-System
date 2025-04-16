package com.example.testingLogIn.CustomObjects;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedResponse {
    private List<?> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
