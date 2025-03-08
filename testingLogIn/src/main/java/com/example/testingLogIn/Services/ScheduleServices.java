package com.example.testingLogIn.Services;

import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.Models.Schedule;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Repositories.ScheduleRepo;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServices {
    
    @Autowired
    private ScheduleRepo scheduleRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SectionServices sectionService;
    @Autowired
    private SubjectServices subjectService;
    @Autowired
    private StudentSubjectGradeRepo ssgRepo;
    @Autowired
    private sySemesterRepo semRepo;
    
    public int addNewSchedule(ScheduleDTO newSchedule){
        UserModel teacher = getTeacherByName(newSchedule.getTeacherName().toLowerCase());
        Section section = sectionService.getSectionById(newSchedule.getSectionId());
        if(isTeacherSchedConflict(teacher,newSchedule,true))
            return 1;
        else if(isSectionSchedConflict(section, newSchedule,true))
            return 2;

        scheduleRepo.save(ScheduleDTOtoSchedule(newSchedule));
        
        return 0;
    }
    
    public List<ScheduleDTO> getSchedulesByTeacher(String teacherName){
        UserModel teacher = getTeacherByName(teacherName.toLowerCase());
        if(teacher == null){
            throw new NullPointerException();
        }
        return  scheduleRepo.findTeacherchedules(teacher.getStaffId()).stream()
                            .sorted(Comparator
                                    .comparing(Schedule::getDay)
                                    .thenComparing(Schedule::getTimeStart))
                            .map(Schedule::mapper)
                            .collect(Collectors.toList());
    }
    
    public List<ScheduleDTO> getSchedulesBySection(int sectionNum){
        SectionDTO section = sectionService.getSection(sectionNum);
        if(section == null)
            throw new NullPointerException();
        
        return scheduleRepo.findSectionSchedules(section.getNumber()).stream()
                            .sorted(Comparator
                                .comparing(Schedule::getDay)
                                .thenComparing(Schedule::getTimeStart))
                            .map(Schedule::mapper)
                            .toList();
    }
    
        public Map<Integer,ScheduleDTO> getSubjectsUniqeTeacher(int sectionId){
            SectionDTO section = sectionService.getSection(sectionId);
            if(section == null)
                throw new NullPointerException();

            Map<Integer,ScheduleDTO> subjectTeachers = new HashMap<>();
            List<Schedule> sectionScheds = scheduleRepo.findSectionSchedules(section.getNumber()).stream()
                                                    .sorted(Comparator
                                                    .comparing(Schedule::getDay)
                                                    .thenComparing(Schedule::getTimeStart))
                                                    .toList();

            sectionScheds.forEach(sched -> {
                if(!subjectTeachers.containsKey(sched.getSubject().getSubjectNumber()))
                    subjectTeachers.put(sched.getSubject().getSubjectNumber(), sched.mapper());
            });
            for(Integer key : subjectTeachers.keySet()){
                ScheduleDTO sched = subjectTeachers.get(key);
                subjectTeachers.get(key).setGradedCount(ssgRepo.getTotalGraded(
                        sched.getSubjectId(),
                        sched.getSubjectId(), 
                        semRepo.findCurrentActive().getSySemNumber()));
                subjectTeachers.get(key).setToBeGradedCount(ssgRepo.getGradesBySectionSubjectSem(
                        sched.getSubjectId(),
                        sched.getSubjectId(), 
                        semRepo.findCurrentActive().getSySemNumber()).size());
            }

            return subjectTeachers;
    }
    
    public int updateSchedule(ScheduleDTO schedDTO){
        Schedule toUpdate = scheduleRepo.findById(schedDTO.getScheduleNumber()).orElse(null);
        
        if(toUpdate != null && toUpdate.isNotDeleted()){
            Schedule updated = ScheduleDTOtoSchedule(schedDTO);
            UserModel teacher = updated.getTeacher();
            Section section = updated.getSection();
            if(isTeacherSchedConflict(teacher,schedDTO,false)){
                return 1;
            }else if(isSectionSchedConflict(section, schedDTO,false)){
                return 2;
            }
            
            toUpdate.setTeacher(updated.getTeacher());
            toUpdate.setSubject(updated.getSubject());
            toUpdate.setSection(updated.getSection());
            toUpdate.setDay(updated.getDay());
            toUpdate.setTimeStart(updated.getTimeStart());
            toUpdate.setTimeEnd(updated.getTimeEnd());
            
            scheduleRepo.save(toUpdate);
            return 3;
            
        }
        return 4;
    }
    
    public boolean deleteSchedule(int schedNum){
        Schedule sched = scheduleRepo.findById(schedNum).orElse(null);
        if(sched != null && sched.isNotDeleted()){
            sched.setNotDeleted(false);
            scheduleRepo.save(sched);
            return true;
        }
        return false;
    }
    
    private boolean isTeacherSchedConflict(UserModel teacher, ScheduleDTO sDTO,boolean isForAdd){
        if(isForAdd)
            return scheduleRepo.isTeacherConflict(
                                                null, 
                                                teacher.getStaffId(), 
                                                sDTO.getDay(),
                                                sDTO.getTimeStart(), 
                                                sDTO.getTimeEnd());
        else
            return scheduleRepo.isTeacherConflict(
                                            sDTO.getScheduleNumber(), 
                                                teacher.getStaffId(),
                                                sDTO.getDay(),
                                                sDTO.getTimeStart(), 
                                                sDTO.getTimeEnd());
    }
    
    private boolean isSectionSchedConflict(Section section, ScheduleDTO sDTO,boolean isForAdd){
        if(isForAdd)
        return scheduleRepo.isSectionConflict(
                                                null, 
                                                section.getNumber(),
                                                sDTO.getDay(),
                                                sDTO.getTimeStart(), 
                                                sDTO.getTimeEnd());
        else
            return scheduleRepo.isSectionConflict(
                                                sDTO.getScheduleNumber(), 
                                                section.getNumber(),
                                                sDTO.getDay(),
                                                sDTO.getTimeStart(), 
                                                sDTO.getTimeEnd());
    }
    private Schedule ScheduleDTOtoSchedule(ScheduleDTO schedDTO){
        return Schedule.builder()
                       .teacher(getTeacherByName(schedDTO.getTeacherName().toLowerCase()))
                       .subject(subjectService.getByName(schedDTO.getSubject().toLowerCase()))
                       .section(sectionService.getSectionById(schedDTO.getSectionId()))
                       .day(schedDTO.getDay())
                       .timeStart(schedDTO.getTimeStart())
                       .timeEnd(schedDTO.getTimeEnd())
                       .isNotDeleted(true)
                       .build();
    }
    
    private UserModel getTeacherByName(String teacherName){
        return userRepo.findAll().stream()
                            .filter(t -> t.isNotDeleted() &&
                                        t.getRole().equals(Role.TEACHER) &&
                                       teacherName.toLowerCase().contains(t.getFirstname().toLowerCase()) &&
                                       teacherName.toLowerCase().contains(t.getLastname().toLowerCase()))
                            .findFirst().orElse(null);
    }
}
