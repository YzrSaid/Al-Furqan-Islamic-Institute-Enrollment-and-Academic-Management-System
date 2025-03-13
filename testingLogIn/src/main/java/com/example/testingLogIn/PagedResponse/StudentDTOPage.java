package com.example.testingLogIn.PagedResponse;

import com.example.testingLogIn.ModelDTO.StudentDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class StudentDTOPage {
    private List<StudentDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
