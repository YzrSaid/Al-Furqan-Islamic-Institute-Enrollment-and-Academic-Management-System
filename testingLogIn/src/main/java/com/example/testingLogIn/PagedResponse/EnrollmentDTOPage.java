package com.example.testingLogIn.PagedResponse;

import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EnrollmentDTOPage {
    private List<EnrollmentDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
