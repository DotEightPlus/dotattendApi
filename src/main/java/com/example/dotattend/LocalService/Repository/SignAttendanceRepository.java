package com.example.dotattend.LocalService.Repository;

import com.example.dotattend.LocalService.Attendance.AttendanceModel;
import com.example.dotattend.LocalService.Attendance.SignAttendanceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SignAttendanceRepository extends JpaRepository<SignAttendanceModel,String> {
    @Query(value ="select * from attendance where attendanceid=?1 and matricno=?2 and fullname=?3 and dept=?4 and level=?5",nativeQuery = true)
    SignAttendanceModel checkIfStudentHasSignedAttendance(String id, String matricno, String fullname, String dept, String level);
}
