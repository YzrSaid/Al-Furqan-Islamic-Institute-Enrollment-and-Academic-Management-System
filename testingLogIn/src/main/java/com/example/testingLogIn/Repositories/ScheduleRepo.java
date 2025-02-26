package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Schedule;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Integer>{
    
    @Query("SELECT COUNT(*) > 0 from Schedule s "+
            "WHERE s.isNotDeleted = true "+
            "AND s.teacher.staffId = :teacherId "+
            "AND (:schedNum IS NULL OR s.scheduleNumber != :schedNum) "+
            "AND ((s.timeStart <= :timeStart AND s.timeEnd > :timeStart) "+
            "OR (s.timeStart > :timeStart AND s.timeStart < :timeEnd))")
    public boolean isTeacherConflict(
            @Param("schedNum") Integer schedNum,
            @Param("teacherId") int teacherId,
            @Param("timeStart") LocalTime timeStart,
            @Param("timeEnd") LocalTime timeEnd);
    
    @Query("SELECT COUNT(*) > 0 from Schedule s "+
            "WHERE s.isNotDeleted = true "+
            "AND s.section.number = :sectionNum "+
            "AND (:schedNum IS NULL OR s.scheduleNumber != :schedNum) "+
            "AND ((s.timeStart <= :timeStart AND s.timeEnd > :timeStart) "+
            "OR (s.timeStart > :timeStart AND s.timeStart < :timeEnd))")
    public boolean isSectionConflict(
            @Param("schedNum") Integer schedNum,
            @Param("sectionNum") int sectionNum,
            @Param("timeStart") LocalTime timeStart,
            @Param("timeEnd") LocalTime timeEnd);
}
