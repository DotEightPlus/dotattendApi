package com.example.dotattend.LocalService.Repository;

import com.example.dotattend.LocalService.Attendance.AttendanceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.ArrayList;

public interface AttendanceRepository extends JpaRepository<AttendanceModel,String> {
    @Query(value = "Select * from preattendance where level=?1 and dept=?2 and session=?3 and semester=?4" ,nativeQuery = true)
    ArrayList<AttendanceModel> getAvailableAttendance(String level, String dept, String session, String semester);


}
