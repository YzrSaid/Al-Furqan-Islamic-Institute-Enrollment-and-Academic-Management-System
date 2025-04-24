package com.example.testingLogIn.CustomObjects;

import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Subject;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EvaluationStatus {
    public UserDTO teacher;
    public Subject subject;
    public Section section;
    public int gradedCount;
    public int totalToBeGraded;

    public EvaluationStatus(UserModel teacher, Section section, Subject subject) {
        if(teacher != null){
            this.teacher = teacher.mapperDTO();
        }
        this.section = section;
        this.subject = subject;
    }
}
