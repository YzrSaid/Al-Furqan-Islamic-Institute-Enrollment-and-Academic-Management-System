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
import java.time.LocalTime;
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
            System.out.println("The section is "+section);
            if(isTeacherSchedConflict(teacher,SchedDTO)){
                isValid = false;
                rejectedSchedules.put(SchedDTO, "Conflict with the Teacher's other existing schedule");
            }else if(isSectionSchedConflict(section, SchedDTO)){
                isValid = false;
                rejectedSchedules.put(SchedDTO, "Conflict with the Section's other existing schedule");
            }
            
            if(isValid){
                scheduleRepo.save(ScheduleDTOtoSchedule(SchedDTO));
            }
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
        Section section = sectionService.getSectionByName(sectionName);
        if(section == null)
            throw new NullPointerException();
        
        return scheduleRepo.findAll().stream()
                           .filter(sched -> sched.isNotDeleted() &&
                                            sched.getSection().getSectionName().toLowerCase()
                                                    .equals(section.getSectionName().toLowerCase()))
                            .map(Schedule::mapper)
                            .toList();
    }
    
    private boolean isTeacherSchedConflict(UserModel teacher, ScheduleDTO sDTO){
        Schedule scheds = scheduleRepo.findAll().stream()
                           .filter(sched -> sched.isNotDeleted() && 
                                            sched.getTeacher().getStaffId() == teacher.getStaffId() &&
                                            (isConflict2(sched,sDTO)))
                            .findFirst().orElse(null);
        return scheds == null;
    }
    
    private boolean isSectionSchedConflict(Section section, ScheduleDTO sDTO){
        return scheduleRepo.findAll().stream()
                           .filter(sched -> sched.isNotDeleted() &&
                                   
                                            sched.getSection().getNumber() == 
                                                    section.getNumber() &&
                                            (isConflict2(sched,sDTO)))
                           .findFirst().orElse(null) == null;
    }
    
    public boolean isConflict(LocalTime timeStart,LocalTime timeEnd, LocalTime time){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
        return (time.isAfter(timeStart) && time.isBefore(timeEnd)) ||
                (time.equals(timeStart) || time.equals(timeEnd));
    }
    
    public boolean isConflict2(Schedule sched,ScheduleDTO schedDTO){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
        return (schedDTO.getTimeStart().isAfter(sched.getTimeStart()) && schedDTO.getTimeStart().isBefore(sched.getTimeEnd())) ||
                (schedDTO.getTimeEnd().isAfter(sched.getTimeStart()) && schedDTO.getTimeEnd().isBefore(sched.getTimeEnd())) ||
                (schedDTO.getTimeStart().equals(sched.getTimeStart()) || schedDTO.getTimeStart().equals(sched.getTimeEnd())) ||
                (schedDTO.getTimeEnd().equals(sched.getTimeStart()) || schedDTO.getTimeEnd().equals(sched.getTimeEnd()));
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
