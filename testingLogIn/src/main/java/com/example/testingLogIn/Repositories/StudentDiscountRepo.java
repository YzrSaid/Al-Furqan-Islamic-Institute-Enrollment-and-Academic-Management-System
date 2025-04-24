package com.example.testingLogIn.Repositories;

import com.example.testingLogIn.CustomObjects.StudentDiscountHandler;
import com.example.testingLogIn.CustomObjects.StudentTotalDiscount;
import com.example.testingLogIn.AssociativeModels.StudentDiscount;
import com.example.testingLogIn.Models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentDiscountRepo extends JpaRepository<StudentDiscount,Integer> {
    
    @Query("SELECT NEW com.example.testingLogIn.CustomObjects.StudentTotalDiscount(SUM(sd.discount.percentageDiscount),SUM(sd.discount.fixedDiscount)) "+
           "FROM StudentDiscount sd "+
            "JOIN sd.student stud "+
           "WHERE sd.discount.isNotDeleted "+
           "AND sd.isNotDeleted "+
           "AND stud.studentId = :studentId")
    Optional<StudentTotalDiscount> getStudentTotalDiscount(@Param("studentId") int studentId);

    @Query("SELECT sd FROM StudentDiscount sd " +
            "JOIN sd.student stud "+
            "WHERE sd.discount.isNotDeleted "+
            "AND sd.discount.discountId = :discountId "+
            "AND stud.studentId = :studentId")
    Optional<StudentDiscount> findStudentDiscountRecord(@Param("studentId") int studentId, @Param("discountId") int discountId);

    @Query("SELECT studDisc FROM StudentDiscount studDisc " +
            "JOIN studDisc.student stud " +
            "JOIN studDisc.discount disc " +
            "WHERE stud.studentId = :studentId")
    List<StudentDiscount> findByStudent(@Param("studentId") int studentId);

    @Query("""
            SELECT s 
            FROM StudentDiscount sd
            INNER JOIN Student s ON s.studentId = sd.student.studentId
            INNER JOIN Enrollment e ON e.student.studentId = s.studentId
            WHERE sd.discount.discountId = :discId
            AND e.enrollmentStatus = 'ENROLLED'
            AND e.SYSemester.sySemNumber = :semester
            """)
    List<Student> findStudentByDiscount(int discId,int semester);

    @Query("SELECT studDisc FROM StudentDiscount studDisc " +
            "JOIN studDisc.student stud " +
            "JOIN studDisc.discount disc " +
            "WHERE stud.studentId = :studentId " +
            "AND studDisc.isNotDeleted")
    List<StudentDiscount> findByStudentNotDeleted(@Param("studentId") int studentId);

    @Query("""
            SELECT NEW com.example.testingLogIn.CustomObjects.StudentDiscountHandler(sd,stud)
            FROM StudentDiscount sd
            LEFT JOIN sd.discount d
            RIGHT JOIN Student stud ON sd.student.studentId = stud.studentId
            AND (:discountId IS NULL OR sd.discount.discountId = :discountId)
            WHERE (d IS NULL OR d.isNotDeleted)
            AND LOWER(stud.fullName) LIKE :search
            AND (CASE WHEN sd.connectionId IS NULL THEN FALSE
                    ELSE sd.isNotDeleted END) = :didAvailed
            """)
    Page<StudentDiscountHandler> findRecords(@Param("discountId")  Integer discountId,
                                                                    boolean didAvailed,
                                                                    String search,
                                                                    Pageable pageable);
}
