package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.EvaluationStatus;
import com.example.testingLogIn.CustomObjects.SubjectSectionCount;
import com.example.testingLogIn.Models.Schedule;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Integer>{
    
    @Query("""
            SELECT COUNT(*) > 0 from Schedule s
            JOIN s.subject subj
            WHERE s.isNotDeleted
            AND subj.isNotDeleted
            AND s.section.isNotDeleted
            AND s.teacher.staffId = :teacherId
            AND s.day = :dayOfWeek
            AND (:schedNum IS NULL OR s.scheduleNumber != :schedNum)
            AND ((s.timeStart <= :timeStart AND s.timeEnd > :timeStart)
            OR (s.timeStart > :timeStart AND s.timeStart < :timeEnd))
            """)
    public boolean isTeacherConflict(
            @Param("schedNum") Integer schedNum,
            @Param("teacherId") int teacherId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("timeStart") LocalTime timeStart,
            @Param("timeEnd") LocalTime timeEnd);
    
    @Query("""
            SELECT s FROM Schedule s
            JOIN s.subject subj
            WHERE s.isNotDeleted
            AND subj.isNotDeleted
            AND s.section.isNotDeleted
            AND s.teacher.staffId = :teacherId
            """)
    List<Schedule> findTeacherchedules(@Param("teacherId") int teacherId);
    
    @Query("""
            SELECT COUNT(*) > 0 from Schedule s
            JOIN s.section sec
            JOIN s.subject subj
            WHERE s.isNotDeleted
            AND subj.isNotDeleted
            AND sec.isNotDeleted
            AND sec.number = :sectionNum
            AND s.day = :dayOfWeek
            AND (:schedNum IS NULL OR s.scheduleNumber != :schedNum)
            AND ((s.timeStart <= :timeStart AND s.timeEnd > :timeStart)
            OR (s.timeStart > :timeStart AND s.timeStart < :timeEnd))
            """)
    boolean isSectionConflict(
            @Param("schedNum") Integer schedNum,
            @Param("sectionNum") int sectionNum,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("timeStart") LocalTime timeStart,
            @Param("timeEnd") LocalTime timeEnd);
    
    @Query("""
           SELECT s FROM Schedule s
           JOIN s.subject subj
           JOIN s.section sec
           WHERE s.isNotDeleted = true
           AND subj.isNotDeleted
           AND sec.isNotDeleted
           AND sec.number = :sectionNum
            """)
    List<Schedule> findSectionSchedules(@Param("sectionNum") int sectionNum);
    
    @Query("""
            SELECT s FROM Schedule s
            WHERE s.isNotDeleted
            AND s.section.number = :sectionId
            AND s.section.isNotDeleted
            AND s.teacher.staffId != :teacherId
            AND s.subject.subjectNumber = :subjectId
            """)
    List<Schedule> findSubjectSectionSchedule(@Param("subjectId") int subjectId, @Param("sectionId") int sectionId, @Param("teacherId") int teacherId);//check if subject is being taught by another teacher

    @Query("""
            SELECT s FROM Schedule s
            WHERE s.isNotDeleted
            AND s.section.number = :sectionId
            AND s.section.isNotDeleted
            AND s.teacher.staffId = :teacherId
            AND s.subject.subjectNumber = :subjectId
            """)
    List<Schedule> findTeacherSectionSubjectSchedule(int subjectId, int sectionId, int teacherId);
    
    @Query("""
            SELECT COUNT(DISTINCT s.subject.subjectNumber) FROM Schedule s
            JOIN s.subject subj
            JOIN s.section sec
            WHERE s.isNotDeleted
            AND subj.isNotDeleted
            AND sec.isNotDeleted
            AND sec.number = :sectionId
            """)
    Integer getUniqueSubjectCountBySection(@Param("sectionId") int sectionId);

    @Query("""
       SELECT NEW com.example.testingLogIn.CustomObjects.SubjectSectionCount(sc.subject, COUNT(DISTINCT sc.section)) 
       FROM Schedule sc
       WHERE sc.isNotDeleted = TRUE
       AND sc.section.isNotDeleted
       AND sc.teacher.staffId = :teacherId
       GROUP BY sc.subject, sc.subject.subjectNumber, sc.subject.subjectName
       """)
    List<SubjectSectionCount> findTeacherSubjectAndSectionCount(@Param("teacherId") int teacherId);

    @Query("""
       SELECT sc FROM Schedule sc
       JOIN sc.subject subj
       WHERE sc.isNotDeleted = TRUE
       AND sc.section.isNotDeleted
       AND sc.teacher.staffId = :teacherId
       AND subj.subjectNumber = :subjectId
       GROUP BY sc.section, sc.id, sc.isNotDeleted, sc.subject, sc.teacher
       """)
    List<Schedule> findByTeacherSubject(@Param("teacherId") int teacherId, @Param("subjectId") int subjectId);

    @Query("""
            SELECT COUNT(sc) FROM Schedule sc
            WHERE sc.isNotDeleted = TRUE
            AND sc.section.isNotDeleted
            AND sc.teacher.staffId = :teacherId
            AND sc.subject.subjectNumber = :subjectId
            """)
    Integer countTeacherSubjectSched(@Param("teacherId") int userId, @Param("subjectId") int subjectId);

    @Query("""
            SELECT s FROM Schedule s
            WHERE s.isNotDeleted
            AND s.section.number = :sectionId
            AND s.section.isNotDeleted
            AND s.subject.subjectNumber = :subjectId
            """)
    Optional<Schedule> findSubjectSectionSchedule(int subjectId, int sectionId);
}
