package com.example.testingLogIn.PagedResponse;

import com.example.testingLogIn.CustomObjects.EnrollmentHandler;
import com.example.testingLogIn.ModelDTO.EnrollmentDTO;
import com.example.testingLogIn.ModelDTO.StudentDTO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

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

    static public EnrollmentDTOPage buildMe(Page<EnrollmentHandler> page, List<EnrollmentDTO> pageContent){
        return builder()
                .content(pageContent)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getNumberOfElements())
                .isLast(page.isLast())
                .build();
    }
}
