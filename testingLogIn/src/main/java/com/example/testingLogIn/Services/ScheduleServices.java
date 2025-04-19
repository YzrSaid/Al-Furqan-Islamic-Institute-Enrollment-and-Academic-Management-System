package com.example.testingLogIn.Services;

import com.example.testingLogIn.CustomObjects.EvaluationStatus;
import com.example.testingLogIn.CustomObjects.SubjectSectionCount;
import com.example.testingLogIn.Enums.Role;
import com.example.testingLogIn.ModelDTO.ScheduleDTO;
import com.example.testingLogIn.ModelDTO.SectionDTO;
import com.example.testingLogIn.Models.Schedule;
import com.example.testingLogIn.Models.SchoolYearSemester;
import com.example.testingLogIn.Models.Section;
import com.example.testingLogIn.Models.Subject;
import com.example.testingLogIn.Repositories.ScheduleRepo;
import com.example.testingLogIn.Repositories.StudentSubjectGradeRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ScheduleServices {

    private final ScheduleRepo scheduleRepo;
    private final UserRepo userRepo;
    private final SectionServices sectionService;
    private final SubjectServices subjectService;
    private final StudentSubjectGradeRepo ssgRepo;
    private final sySemesterRepo semRepo;

    @Autowired
    public ScheduleServices(ScheduleRepo scheduleRepo, UserRepo userRepo, SectionServices sectionService, SubjectServices subjectService, StudentSubjectGradeRepo ssgRepo, sySemesterRepo semRepo) {
        this.scheduleRepo = scheduleRepo;
        this.userRepo = userRepo;
        this.sectionService = sectionService;
        this.subjectService = subjectService;
        this.ssgRepo = ssgRepo;
        this.semRepo = semRepo;
    }

    public int addNewSchedule(ScheduleDTO newSchedule){
        UserModel teacher = getTeacherByName(newSchedule.getTeacherName().toLowerCase());
        Section section = sectionService.getSectionById(newSchedule.getSectionId());
        List<Schedule> res = scheduleRepo.findSubjectSectionSchedule(subjectService.getByName(newSchedule.getSubject().toLowerCase()).getSubjectNumber()
                                                                                            ,section.getNumber(),
                                                                                            teacher.getStaffId());
        if(isTeacherSchedConflict(teacher,newSchedule,true))
            return 1;
        else if(isSectionSchedConflict(section, newSchedule,true))
            return 2;
        else if(!res.isEmpty())
            return 3;
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

    public List<ScheduleDTO> getLoggedInTeacherSched(){
        UserModel teacher = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            teacher = (UserModel)authentication.getPrincipal();
        }
        if(teacher != null)
            return scheduleRepo.findTeacherchedules(teacher.getStaffId()).stream()
                    .sorted(Comparator
                            .comparing(Schedule::getDay)
                            .thenComparing(Schedule::getTimeStart))
                    .map(Schedule::mapper)
                    .collect(Collectors.toList());

        return null;
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
    public List<EvaluationStatus> getSectionSubjects(Integer sectionId){
        SchoolYearSemester sem = semRepo.findCurrentActive();
        int semId = Optional.ofNullable(sem).map(SchoolYearSemester::getSySemNumber).orElse(0);
        if(semId == 0)
            return null;
        return ssgRepo.findSectionSubjects(sectionId,semId).stream()
                .peek(es -> {
                    es.setGradedCount(ssgRepo.getTotalGraded(
                            sectionId,
                            es.getSubject().getSubjectNumber(),
                            semRepo.findCurrentActive().getSySemNumber()));
                    es.setTotalToBeGraded(ssgRepo.getGradesBySectionSubjectSem(
                            sectionId,
                            es.getSubject().getSubjectNumber(),
                            semRepo.findCurrentActive().getSySemNumber()).size());
                }).toList();
    }

    public int updateSchedule(ScheduleDTO schedDTO){
        Schedule toUpdate = scheduleRepo.findById(schedDTO.getScheduleNumber()).orElse(null);
        UserModel t = getTeacherByName(schedDTO.getTeacherName().toLowerCase());
        List<Schedule> res = scheduleRepo.findSubjectSectionSchedule(subjectService.getByName(schedDTO.getSubject().toLowerCase()).getSubjectNumber()
                ,toUpdate.getSection().getNumber(),t.getStaffId());
        if(toUpdate != null && toUpdate.isNotDeleted()){
            Schedule updated = ScheduleDTOtoSchedule(schedDTO);
            UserModel teacher = updated.getTeacher();
            Section section = updated.getSection();
            if(isTeacherSchedConflict(teacher,schedDTO,false)){
                return 1;
            }else if(isSectionSchedConflict(section, schedDTO,false)){
                return 2;
            }else if(!res.isEmpty())
                return 3;
            
            toUpdate.setTeacher(updated.getTeacher());
            toUpdate.setSubject(updated.getSubject());
            toUpdate.setSection(updated.getSection());
            toUpdate.setDay(updated.getDay());
            toUpdate.setTimeStart(updated.getTimeStart());
            toUpdate.setTimeEnd(updated.getTimeEnd());
            
            scheduleRepo.save(toUpdate);
            return 4;
            
        }
        return 5;
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

    //FOR TEACHER UNIQUE SUBJECTS HANDLING
    public List<SubjectSectionCount> getTeacherSubjects(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return null;

        UserModel teacher =(UserModel) authentication.getPrincipal();
        return scheduleRepo.findTeacherSubjectAndSectionCount(teacher.getStaffId());
    }

    public List<ScheduleDTO> getSectionsBySubject(int subjectId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return null;
        UserModel teacher =(UserModel) authentication.getPrincipal();

        Integer subjectCount = Optional.ofNullable(scheduleRepo.countTeacherSubjectSched(teacher.getStaffId(),subjectId)).orElse(0);
        if(subjectCount == 0)
            throw new NullPointerException();

        Map<Integer,ScheduleDTO> scheduleList = scheduleRepo.findByTeacherSubject(teacher.getStaffId(),subjectId).stream()
                .collect(Collectors.toMap(sched -> sched.getSubject().getSubjectNumber(),Schedule::mapper));
        for(Integer key : scheduleList.keySet()){
            ScheduleDTO sched = scheduleList.get(key);
            Integer toBeGraded = ssgRepo.getGradesBySectionSubjectSem(
                    sched.getSectionId(),
                    sched.getSubjectId(),
                    semRepo.findCurrentActive().getSySemNumber()).size();
            Integer graded = ssgRepo.getTotalGraded(
                    sched.getSectionId(),
                    sched.getSubjectId(),
                    semRepo.findCurrentActive().getSySemNumber());
            scheduleList.get(key).setGradedCount(graded);
            scheduleList.get(key).setToBeGradedCount(toBeGraded);
        }

        return scheduleList.values().stream().toList();
    }
}
