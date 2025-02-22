package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.Models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepo extends JpaRepository<Schedule, Integer>{
    
}
