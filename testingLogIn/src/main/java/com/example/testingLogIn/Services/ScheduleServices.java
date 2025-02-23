package com.example.testingLogIn.Services;

import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.Models.Schedule;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Subject;
import com.example.testingLogIn.Models.Teacher;
import com.example.testingLogIn.Repositories.ScheduleRepo;
import com.example.testingLogIn.Repositories.SubjectRepo;
import com.example.testingLogIn.Repositories.TeacherRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import java.time.format.DateTimeFormatter;
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
    private TeacherRepo teacherRepo;
    @Autowired
    private SectionServices sectionService;
    @Autowired
    private SubjectRepo subjectRepo;
    @Autowired
    private SubjectServices subjectService;
    
    public Map<ScheduleDTO,String> addNewSchedules(List<ScheduleDTO> newSchedules){
        Map<ScheduleDTO,String> rejectedSchedules = new HashMap();
        
        newSchedules.forEach(SchedDTO ->{
            boolean isValid=true;
            UserModel teacher = getTeacherByName(SchedDTO.getTeacherName().toLowerCase()).getUser();
            Section section = sectionService.getSectionByName(SchedDTO.getSectionName().toLowerCase());
            if(isTeacherSchedConflict(teacher,SchedDTO,true)){
                isValid = false;
                rejectedSchedules.put(SchedDTO, "Conflict with the Teacher's other existing schedule");
            }else if(isSectionSchedConflict(section, SchedDTO,true)){
                isValid = false;
                rejectedSchedules.put(SchedDTO, "Conflict with the Section's other existing schedule");
            }
            
            if(isValid)
                scheduleRepo.save(ScheduleDTOtoSchedule(SchedDTO));
            
        });
        
        return rejectedSchedules;
    }
    
    public List<ScheduleDTO> getSchedulesByTeacher(String teacherName){
        Teacher teacher = getTeacherByName(teacherName.toLowerCase());
        if(teacher == null){
            throw new NullPointerException();
        }
        return  scheduleRepo.findAll().stream()
                            .filter(sched -> sched.isNotDeleted() &&
                                            sched.getTeacher().getStaffId() == teacher.getUser().getStaffId())
                            .map(Schedule::mapper)
                            .collect(Collectors.toList());
    }
    
    public List<ScheduleDTO> getSchedulesBySection(String sectionName){
        Section section = sectionService.getSectionByName(sectionName.toLowerCase());
        if(section == null)
            throw new NullPointerException();
        
        return scheduleRepo.findAll().stream()
                           .filter(sched -> sched.isNotDeleted() &&
                                            sched.getSection().getSectionName().toLowerCase()
                                                    .equals(section.getSectionName().toLowerCase()))
                            .map(Schedule::mapper)
                            .toList();
    }
    
    public int updateSchedule(ScheduleDTO schedDTO){
        Schedule toUpdate = scheduleRepo.findById(schedDTO.getScheduleNumber()).orElse(null);
        
        if(toUpdate != null){
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
    
    private boolean isTeacherSchedConflict(UserModel teacher, ScheduleDTO sDTO,boolean isForAdd){
        if(isForAdd)
            return scheduleRepo.findAll().stream()
                               .filter(sched -> sched.isNotDeleted() && 
                                                sched.getTeacher().getStaffId() == teacher.getStaffId() 
                                       &&(isConflict2(sched,sDTO))
                               )
                                .findFirst().orElse(null) != null;
        else
            return scheduleRepo.findAll().stream()
                               .filter(sched -> sched.isNotDeleted() &&
                                                sched.getScheduleNumber() != sDTO.getScheduleNumber() &&
                                                sched.getTeacher().getStaffId() == teacher.getStaffId() 
                                       &&(isConflict2(sched,sDTO))
                               )
                                .findFirst().orElse(null) != null;
    }
    
    private boolean isSectionSchedConflict(Section section, ScheduleDTO sDTO,boolean isForAdd){
        if(isForAdd)
        return scheduleRepo.findAll().stream()
                           .filter(sched -> sched.isNotDeleted() &&
                                            sched.getSection().getNumber() == 
                                                    section.getNumber() 
                                   &&(isConflict2(sched,sDTO))
                           )
                           .findFirst().orElse(null) != null;
        else
            return scheduleRepo.findAll().stream()
                               .filter(sched -> sched.isNotDeleted() &&
                                                sched.getScheduleNumber() != sDTO.getScheduleNumber() &&
                                                sched.getSection().getNumber() == 
                                                    section.getNumber() &&
                                                (isConflict2(sched,sDTO))
                               )
                                .findFirst().orElse(null) != null;
    }
    
    public boolean isConflict2(Schedule sched,ScheduleDTO schedDTO){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
        return (schedDTO.getTimeStart().isAfter(sched.getTimeStart()) && schedDTO.getTimeStart().isBefore(sched.getTimeEnd())) ||
                (schedDTO.getTimeEnd().isAfter(sched.getTimeStart()) && schedDTO.getTimeEnd().isBefore(sched.getTimeEnd())) ||
                
                (sched.getTimeStart().isAfter(schedDTO.getTimeStart()) && sched.getTimeStart().isBefore(schedDTO.getTimeEnd())) ||
                (sched.getTimeEnd().isAfter(schedDTO.getTimeStart()) && sched.getTimeEnd().isBefore(schedDTO.getTimeEnd())) ||
                sched.getTimeStart().equals(schedDTO.getTimeStart())
                ;
    }
    
    private Schedule ScheduleDTOtoSchedule(ScheduleDTO schedDTO){
        return Schedule.builder()
                       .teacher(getTeacherByName(schedDTO.getTeacherName().toLowerCase()).getUser())
                       .subject(subjectService.getByName(schedDTO.getSubject().toLowerCase()))
                       .section(sectionService.getSectionByName(schedDTO.getSectionName().toLowerCase()))
                       .day(schedDTO.getDay())
                       .timeStart(schedDTO.getTimeStart())
                       .timeEnd(schedDTO.getTimeEnd())
                       .isNotDeleted(true)
                       .build();
    }
    
    private Teacher getTeacherByName(String teacherName){
        Teacher teacher = teacherRepo.findAll().stream()
                          .filter(t -> t.isNotDeleted() &&
                                       teacherName.toLowerCase().contains(t.getUser().getFirstname().toLowerCase()) &&
                                       teacherName.toLowerCase().contains(t.getUser().getLastname().toLowerCase()))
                          .findFirst().orElse(null);
        return teacher;
    }
}
