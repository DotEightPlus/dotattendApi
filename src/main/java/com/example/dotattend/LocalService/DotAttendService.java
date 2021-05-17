package com.example.dotattend.LocalService;

import com.example.dotattend.LocalService.Attendance.AttendanceModel;
import com.example.dotattend.LocalService.Attendance.SignAttendanceModel;
import com.example.dotattend.LocalService.CustomException.MyException;
import com.example.dotattend.LocalService.Department.DepartmentModel;
import com.example.dotattend.LocalService.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class DotAttendService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    SignAttendanceRepository signAttendanceRepository;
    @Autowired
    DepartmentsRepository departmentsRepository;

    public User registerThisStudent(User user) {
       return userRepository.save(user);
    }

    public Optional<User> retrieveUserInformation(String matricno) {
        return userRepository.findByMatricNo(matricno);
    }

    public AttendanceModel createattendance(AttendanceModel attendanceModel) throws MyException {
        attendanceModel.setId( UUID.randomUUID().toString());
        attendanceModel.setDatecreated(new Date(System.currentTimeMillis()));
        attendanceModel.setExpiretime(new Date(System.currentTimeMillis()+1000*60*60*attendanceModel.getDuration()));
        Path path= Paths.get(System.getProperty("user.dir")+"/config.txt");
        if (Files.exists(path)){
            Properties properties=new Properties();
            try {
                FileInputStream fileInputStream=new FileInputStream(path.toFile());
                properties.load(fileInputStream);
                fileInputStream.close();
                String schoolname=properties.getProperty("school");
                String session=properties.getProperty("session");
                String semester=properties.getProperty("semester");
                attendanceModel.setSchoolname(schoolname);
                attendanceModel.setSession(session);
                attendanceModel.setSemester(semester);
                return attendanceRepository.save(attendanceModel);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new MyException("Server configuration error");
            } catch (IOException e) {
                e.printStackTrace();
                throw new MyException("Server configuration error");
            }
        }else {
            throw new MyException("Server configuration error");
        }

    }

    public ArrayList<AttendanceModel> getAvailableAttendance(String level, String dept) throws MyException {
        //Get all attendance then filter the attendance that has not expired.
        Path path= Paths.get(System.getProperty("user.dir")+"/config.txt");
        if (Files.exists(path)){
            Properties properties=new Properties();
            try {
                FileInputStream fileInputStream=new FileInputStream(path.toFile());
                properties.load(fileInputStream);
                fileInputStream.close();
                String session=properties.getProperty("session");
                String semester=properties.getProperty("semester");
                ArrayList<AttendanceModel> attendance=attendanceRepository.getAvailableAttendance(level,dept,session,semester);
                //This list contains the available attendance
                ArrayList<AttendanceModel> availableAttendance=new ArrayList<>();
                if (attendance!=null){
                    attendance.stream().filter(a->a.getExpiretime().after(new Date(System.currentTimeMillis()))).forEach(availableAttendance::add);

                    return availableAttendance;
                }else {
                    throw new MyException("No attendance found, click on create attendance to create one");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new MyException("Server configuration error");
            } catch (IOException e) {
                e.printStackTrace();
                throw new MyException("Server configuration error");
            }
        }else {
            throw new MyException("Server configuration error");
        }

    }

    public SignAttendanceModel signAttendance(SignAttendanceModel signAttendanceModel) throws MyException {
        SignAttendanceModel result=null;
        System.out.println("[DOT ATTEND SERVICE]--Checking if student has signed this attendance before -->>>>>> Checking ..");
        SignAttendanceModel signAttendance=signAttendanceRepository.checkIfStudentHasSignedAttendance(signAttendanceModel.getAttendanceid(),signAttendanceModel.getMatricno(),signAttendanceModel.getFullname(),signAttendanceModel.getDept(),signAttendanceModel.getLevel());
        if (signAttendance==null){
            //get the attendance info from preattendance table and save it in the attendance table
            Optional<AttendanceModel> attendanceModel1=attendanceRepository.findById(signAttendanceModel.getAttendanceid());
            attendanceModel1.orElseThrow(()->new MyException("No such attendance"));
            signAttendanceModel.setCoursecode(attendanceModel1.get().getCoursecode());
            signAttendanceModel.setCoursetitle(attendanceModel1.get().getCoursetitle());
            Path path= Paths.get(System.getProperty("user.dir")+"/config.txt");
            if (Files.exists(path)) {
                Properties properties = new Properties();
                try {
                    FileInputStream fileInputStream = new FileInputStream(path.toFile());
                    properties.load(fileInputStream);
                    fileInputStream.close();
                    String session = properties.getProperty("session");
                    String semester = properties.getProperty("semester");
                    signAttendanceModel.setSession(session);
                    signAttendanceModel.setSemester(semester);
                    signAttendanceModel.setLevel(attendanceModel1.get().getLevel());
                    signAttendanceModel.setDatecreated(attendanceModel1.get().getDatecreated());
                    signAttendanceModel.setExpiretime(attendanceModel1.get().getExpiretime());
                    result=signAttendanceRepository.save(signAttendanceModel);
                    return result;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new MyException("Server configuration error");
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new MyException("Server configuration error");
                }
            }else{
                throw new MyException("Server configuration error");
            }


        }else {
            System.out.println("[DOT ATTEND SERVICE]--Checking if student has signed this attendance before -->>>>>> Student have signed this attendance");
            throw new MyException("You have signed this attendance");
        }
    }

    public List<DepartmentModel> getDepartments() {
        return departmentsRepository.findAll();
    }
}
