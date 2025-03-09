package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.StudentSubjectGrade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author magno
 */
@Repository
public interface StudentSubjectGradeRepo extends JpaRepository<StudentSubjectGrade, Integer> {
    
    @Query("SELECT CASE WHEN EXISTS ( " +
           "SELECT 1 FROM StudentSubjectGrade sg " +
           "WHERE sg.student.studentId = :studentId " +
           "AND sg.section.level.levelId = :gradeLevelId " +
           "GROUP BY sg.semester " +
           "HAVING AVG(sg.subjectGrade) > 74" +
           ") THEN true ELSE false END")
    boolean didStudentPassed(
        @Param("studentId") int studentId,
        @Param("gradeLevelId") int gradeLevelId);

    @Query("SELECT sg FROM StudentSubjectGrade sg "+
           "WHERE sg.section.number = :sectionId "+
           "AND sg.semester.sySemNumber = :semId")
    List<StudentSubjectGrade> getSectionGradesByCurrentSem(
            @Param("sectionId") int sectionId,
            @Param("semId") int semId);

    @Query("SELECT sg FROM StudentSubjectGrade sg "+
           "WHERE sg.student.studentId = :studentId")
    List<StudentSubjectGrade> getGradesByStudent(@Param("studentId") int studentId);

    @Query("SELECT sg FROM StudentSubjectGrade sg "+
            "WHERE sg.student.studentId = :studentId "+
            "AND sg.section.level.levelId = :levelId " +
            "ORDER BY sg.semester DESC")
    List<StudentSubjectGrade> getGradesByStudentGradeLevel(@Param("studentId") int studentId,@Param("levelId") int levelid);
    
    @Query("SELECT sg FROM StudentSubjectGrade sg "+
            "WHERE sg.section.number = :sectionId "+
            "AND sg.subject.subjectNumber = :subjectId "+
            "AND sg.semester.sySemNumber = :semId")
    List<StudentSubjectGrade> getGradesBySectionSubjectSem(
            @Param("sectionId") int sectionId,
            @Param("subjectId") int subjectId,
            @Param("semId") int semId);

    @Query("SELECT COUNT(sg) FROM StudentSubjectGrade sg " +
            "WHERE sg.subjectGrade IS NOT NULL " +
            "AND sg.semester.sySemNumber = :semId "+
            "AND sg.section.number = :sectionId "+
            "AND sg.subject.subjectNumber = :subjectId")
    Integer getTotalGraded(
            @Param("sectionId") int sectionId,
            @Param("subjectId") int subjectId,
            @Param("semId") int semId);
}
