package com.example.testingLogIn.CustomComparator;

import com.example.testingLogIn.ModelDTO.SectionDTO;

import java.util.Comparator;
import java.util.Optional;

public class SectionComparator implements Comparator<SectionDTO> {
    @Override
    public int compare(SectionDTO o1, SectionDTO o2) {
        return compareSectionName(o1, o2);
    }

    public int myMethodFactory(SectionDTO sec1,SectionDTO sec2,String compareBy){
        switch (compareBy){
            case "ENROLLED_COUNT":
                return compareStudentCount(sec1,sec2);
            case "GRADE_LEVEL":
                return compareLevelName(sec1,sec2);
            default:
                return compareSectionName(sec1,sec2);

        }
    }
    private int compareStudentCount(SectionDTO sec1,SectionDTO sec2){
        return Optional.ofNullable(sec2.getStudentEnrolledCount()).orElse(0)
                .compareTo
                (Optional.ofNullable(sec1.getStudentEnrolledCount()).orElse(0));
    }

    private int compareSectionName(SectionDTO sec1,SectionDTO sec2){
        return Optional.ofNullable(sec1.getSectionName()).orElse("")
                .compareTo
                (Optional.ofNullable(sec2.getSectionName()).orElse(""));
    }

    private int compareLevelName(SectionDTO sec1,SectionDTO sec2){
        return Optional.ofNullable(sec1.getGradeLevelName()).orElse("")
                .compareTo
                (Optional.ofNullable(sec2.getGradeLevelName()).orElse(""));
    }
}
