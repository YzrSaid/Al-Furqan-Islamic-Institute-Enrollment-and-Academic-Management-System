package com.example.testingLogIn.PagedResponse;

import com.example.testingLogIn.AssociativeModels.DistributablesPerStudent;
import com.example.testingLogIn.ModelDTO.DistributablePerStudentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StudentDistributablePage {
    private List<DistributablePerStudentDTO> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
}
