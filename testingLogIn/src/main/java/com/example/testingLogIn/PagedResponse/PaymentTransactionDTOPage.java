package com.example.testingLogIn.PagedResponse;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PaymentTransactionDTOPage {
    private List<?> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
