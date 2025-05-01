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
import com.example.testingLogIn.Repositories.SubjectRepo;
import com.example.testingLogIn.Repositories.sySemesterRepo;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserModel;
import com.example.testingLogIn.WebsiteSecurityConfiguration.UserRepo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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
    private final SubjectRepo subjectRepo;

    @Autowired
    public ScheduleServices(ScheduleRepo scheduleRepo, UserRepo userRepo, SectionServices sectionService, SubjectServices subjectService, StudentSubjectGradeRepo ssgRepo, sySemesterRepo semRepo, SubjectRepo subjectRepo) {
        this.scheduleRepo = scheduleRepo;
        this.userRepo = userRepo;
        this.sectionService = sectionService;
        this.subjectService = subjectService;
        this.ssgRepo = ssgRepo;
        this.semRepo = semRepo;
        this.subjectRepo = subjectRepo;
    }

    public Map<Integer,ScheduleDTO> addNewSchedule(ScheduleDTO newSchedule){
        UserModel teacher = userRepo.findById(newSchedule.getTeacherId()).orElse(null);
        Section section = sectionService.getSectionById(newSchedule.getSectionId());
        List<Schedule> res = scheduleRepo.findSubjectSectionSchedule(newSchedule.getSubjectId(),
                                                                    section.getNumber(),
                                                                    newSchedule.getTeacherId());
        if(isTeacherSchedConflict(teacher,newSchedule,true))
            return Map.of(1,new ScheduleDTO());
        else if(isSectionSchedConflict(section, newSchedule,true))
            return Map.of(2,new ScheduleDTO());
        else if(!res.isEmpty())
            return Map.of(3,new ScheduleDTO());
        Schedule addedSchedule = scheduleRepo.save(ScheduleDTOtoSchedule(newSchedule));

        return Map.of(0,addedSchedule.mapper());
    }

    public List<ScheduleDTO> getSchedulesByTeacher(String teacherName){
        UserModel teacher = userRepo.findByFullName(teacherName.toLowerCase()).orElseThrow(()->new NullPointerException("Teacher Information Not Found"));
        return  scheduleRepo.findTeacherchedules(teacher.getStaffId()).stream()
                            .sorted(Comparator
                                    .comparing(Schedule::getDay)
                                    .thenComparing(Schedule::getTimeStart))
                            .map(Schedule::mapper)
                            .collect(Collectors.toList());
    }

    public InputStreamResource downloadTeacherSchedule(String teacherName){
        UserModel teacher = userRepo.findByFullName(teacherName.toLowerCase()).orElseThrow(()->new NullPointerException("Teacher Information Not Found"));
        List<Schedule> teacherSchedule =  scheduleRepo.findTeacherchedules(teacher.getStaffId()).stream()
                .sorted(Comparator
                        .comparing(Schedule::getDay)
                        .thenComparing(Schedule::getTimeStart)).toList();

        if(teacherSchedule.isEmpty())
            throw new NullPointerException("Selected teacher doest not have a schedule");

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try {
            boas.write("Section,Subject,Start,End\n".getBytes(StandardCharsets.UTF_8));
            for(Schedule schedule : teacherSchedule){
                boas.write(String.format("%s,%s,%s,%s\n",schedule.getSection().toString(),
                            schedule.getSubject().getSubjectName(),
                            NonModelServices.formattedTime(schedule.getTimeStart()),
                            NonModelServices.formattedTime(schedule.getTimeEnd())).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new InputStreamResource(new ByteArrayInputStream(boas.toByteArray()));
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

    public List<?> downloadSectionSchedules(int sectionNum){
        SectionDTO section = sectionService.getSection(sectionNum);

        List<Schedule> schedules = scheduleRepo.findSectionSchedules(section.getNumber()).stream()
                .sorted(Comparator
                        .comparing(Schedule::getDay)
                        .thenComparing(Schedule::getTimeStart)).toList();

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try {
            boas.write("Teacher,Subject,Start,End\n".getBytes(StandardCharsets.UTF_8));
            for(Schedule schedule : schedules){
                boas.write(String.format("%s,%s,%s,%s\n",schedule.getTeacher().getFullName(),
                        schedule.getSubject().getSubjectName(),
                        NonModelServices.formattedTime(schedule.getTimeStart()),
                        NonModelServices.formattedTime(schedule.getTimeEnd())).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return List.of(section.getSectionName()+"_Schedule.csv",new InputStreamResource(new ByteArrayInputStream(boas.toByteArray())));
    }

    public List<ScheduleDTO> getSchedulesBySection(int sectionNum){
        SectionDTO section = sectionService.getSection(sectionNum);

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
        if(sectionId != null)
            return ssgRepo.findSectionSubjects(sectionId,semId).stream()
                    .peek(es -> {
                        int sectionNum = es.getSection().getNumber();
                        int subjectNum = es.getSubject().getSubjectNumber();
                        es.setGradedCount(ssgRepo.getTotalGraded(
                                sectionNum,
                                subjectNum,
                                semId));
                        es.setTotalToBeGraded(ssgRepo.getGradesBySectionSubjectSem(
                                sectionNum,
                                subjectNum,
                                semId).size());
                    }).toList();
        else
            return ssgRepo.findWithUngradedGrades(semId).stream()
                    .peek(es -> {
                        int sectionNum = es.getSection().getNumber();
                        int subjectNum = es.getSubject().getSubjectNumber();
                        es.setGradedCount(ssgRepo.getTotalGraded(
                                sectionNum,
                                subjectNum,
                                semId));
                        es.setTotalToBeGraded(ssgRepo.getGradesBySectionSubjectSem(
                                sectionNum,
                                subjectNum,
                                semId).size());
                    }).toList();
    }

    public ScheduleDTO updateSchedule(ScheduleDTO schedDTO){
        Schedule toUpdate = scheduleRepo.findById(schedDTO.getScheduleNumber()).orElseThrow(()->new NullPointerException("Schedule Record Not Found"));
        UserModel t = userRepo.findById(schedDTO.getTeacherId()).orElseThrow(()->new NullPointerException("Selected teacher not found"));
        List<Schedule> res = scheduleRepo.findSubjectSectionSchedule(schedDTO.getSubjectId()
                ,toUpdate.getSection().getNumber(),t.getStaffId());
        if(toUpdate.isNotDeleted()){
            Schedule updated = ScheduleDTOtoSchedule(schedDTO);
            UserModel teacher = updated.getTeacher();
            Section section = updated.getSection();

            if(isTeacherSchedConflict(teacher,schedDTO,false))
                throw new IllegalArgumentException("Conflict with the Teacher's other existing schedule");
            else if(isSectionSchedConflict(section, schedDTO,false))
                throw new IllegalArgumentException("Conflict with the Section's other existing schedule");
            
            toUpdate.setTeacher(updated.getTeacher());
            toUpdate.setSubject(updated.getSubject());
            toUpdate.setSection(updated.getSection());
            toUpdate.setDay(updated.getDay());
            toUpdate.setTimeStart(updated.getTimeStart());
            toUpdate.setTimeEnd(updated.getTimeEnd());
            
            Schedule updatedSched = scheduleRepo.save(toUpdate);
            return updatedSched.mapper();
            
        }
        throw new NullPointerException("Schedule Record Not Found");
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
                       .teacher(userRepo.findById(schedDTO.getTeacherId()).orElse(null))
                       .subject(subjectRepo.findById(schedDTO.getSubjectId()).orElse(null))
                       .section(sectionService.getSectionById(schedDTO.getSectionId()))
                       .day(schedDTO.getDay())
                       .timeStart(schedDTO.getTimeStart())
                       .timeEnd(schedDTO.getTimeEnd())
                       .isNotDeleted(true)
                       .build();
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
