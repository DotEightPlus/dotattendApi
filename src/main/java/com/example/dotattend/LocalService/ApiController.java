package com.example.dotattend.LocalService;

import com.example.dotattend.LocalService.Attendance.AttendanceModel;
import com.example.dotattend.LocalService.Attendance.SignAttendanceModel;
import com.example.dotattend.LocalService.CustomException.MyException;
import com.example.dotattend.LocalService.Department.DepartmentModel;
import com.example.dotattend.LocalService.JWT.JwtModel;
import com.example.dotattend.LocalService.JWT.JwtUtils;
import com.example.dotattend.LocalService.Model.LoginModel;
import com.example.dotattend.LocalService.Repository.User;
import com.example.dotattend.LocalService.Security.MyUserDetailService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ApiController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private DotAttendService dotAttendService;
    @RequestMapping(value = "hello")
    public String sayhello(){
       return "Welcome to dotattend";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////AUTHENTICATION////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @RequestMapping(value = "authenticate")
    public ResponseEntity<?> authenticate(@RequestBody LoginModel loginModel){
        if (loginModel.getMatricno()!=null&&loginModel.getPassword()!=null){
            try{
                System.out.println("[CONTROLLER]--Authenticating-->>>>>>");
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginModel.getMatricno(),loginModel.getPassword()));
                UserDetails userDetails=myUserDetailService.loadUserByUsername(loginModel.getMatricno());
                if (userDetails!=null){
                    System.out.println("[CONTROLLER]--Generating token-->>>>>>");
                    String token=jwtUtils.generateToken(userDetails);
                    JwtModel jwtModel=new JwtModel();
                    jwtModel.setRole(userDetails.getAuthorities().toString());
                    jwtModel.setToken(token);
                    System.out.println("[CONTROLLER]--Generating token-->>>>>> Generated");
                    return ResponseEntity.ok().body(jwtModel);
                }else {
                    return ResponseEntity.notFound().build();
                }
            }catch (BadCredentialsException e){
                System.out.println("[CONTROLLER]--Authenticating-->>>>>> Bad credentials");
                return ResponseEntity.unprocessableEntity().body("Bad credentials");
            }
        }else {
            return ResponseEntity.badRequest().body("Invalid request");
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////// AUTHENTICATION END ////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////REGISTERATION AND INFORMATION////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResponseEntity<?> registerStudent(@RequestBody User user){
        if (user!=null){
            System.out.println("[CONTROLLER]--Registering-->>>>>>");
            User registerationCopy=dotAttendService.registerThisStudent(user);
            if (registerationCopy!=null){
                System.out.println("[CONTROLLER]--Registering-->>>>>>Registered");
                return ResponseEntity.ok(registerationCopy);
            }else {
                System.out.println("[CONTROLLER]--Registering-->>>>>>Unable to register");
                return ResponseEntity.unprocessableEntity().body("Unable to register");
            }
        }else {
            return ResponseEntity.badRequest().body("Invalid request");
        }
    }
    @RequestMapping(value = "retrieveuserinfo")
    public ResponseEntity<?> retrieveUserInfo(@RequestAttribute String matricno){
        if (matricno!=null){
            System.out.println("[CONTROLLER]--Retrieving user information-->>>>>>");
            Optional<User> user=dotAttendService.retrieveUserInformation(matricno);
            if (user.isPresent()){
                System.out.println("[CONTROLLER]--Retrieving user information-->>>>>> Retrieved");
                return ResponseEntity.ok().body(user);
            }else {
                System.out.println("[CONTROLLER]--Retrieving user information-->>>>>> Unable to retrieved");
                return ResponseEntity.notFound().build();
            }
        }else {
            return ResponseEntity.unprocessableEntity().body("Invalid request");
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////// REGISTERATION AND INFORMATION END ////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////






//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////ATTTENDANCE SECTION //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @RequestMapping(value = "getdepartments")
    public ResponseEntity<?> getDepartment(){
        List<DepartmentModel> departments=dotAttendService.getDepartments();
        if (!departments.isEmpty()){
            return ResponseEntity.ok().body(departments);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
   @RequestMapping(value = "createattendance",method = RequestMethod.POST)
    public ResponseEntity<?> createAttendance(@RequestBody AttendanceModel attendance) throws MyException {
        if (attendance!=null){
            System.out.println("[CONTROLLER]--Creating attendance by -->>>>>> Creating ");
            AttendanceModel result=dotAttendService.createattendance(attendance);
            if (result!=null){
                System.out.println("[CONTROLLER]--Creating attendance by -->>>>>> Created ");
                return ResponseEntity.ok("Success");
            }else {
                System.out.println("[CONTROLLER]--Creating attendance by -->>>>>> Unable to create");
                return ResponseEntity.unprocessableEntity().body("Unable to create attendance");
            }
        }else {
            return ResponseEntity.unprocessableEntity().body("Invalid request");
        }
   }

   @RequestMapping(value = "availableattendance/{level}/{dept}")
    public ResponseEntity<?> getAvailableAttendance(@PathVariable String level,@PathVariable String dept) throws MyException {
        if (level!=null&&dept!=null){
            System.out.println("[CONTROLLER]--getting available attendance by -->>>>>> Getting available attendance ");
            ArrayList<AttendanceModel> attendance=dotAttendService.getAvailableAttendance(level,dept);
            if (!attendance.isEmpty()){
                System.out.println("[CONTROLLER]--getting available attendance by -->>>>>> Attendance retrieved");
                return ResponseEntity.ok().body(attendance);
            }else {
                System.out.println("[CONTROLLER]--getting available attendance by -->>>>>> No available attendance");
                return ResponseEntity.notFound().build();
            }
        }else {
            return ResponseEntity.badRequest().body("Invalid request");
        }

   }
   @RequestMapping(value = "signattendance",method = RequestMethod.POST)
   public ResponseEntity<?> signAttendance(@RequestBody SignAttendanceModel signAttendanceModel,@RequestAttribute String matricno) throws MyException {

        if (signAttendanceModel!=null&&matricno!=null){
            System.out.println("[CONTROLLER]--Signing attendance by -->>>>>> Signing attendance ");
            signAttendanceModel.setMatricno(matricno);
            SignAttendanceModel result=dotAttendService.signAttendance(signAttendanceModel);
            if (result!=null){
                return ResponseEntity.ok().body("Attendance Signed");
            }else {
                return ResponseEntity.unprocessableEntity().body("Cannot sign attendance");
            }
        }else {
            return ResponseEntity.badRequest().body("Invalid request");
        }
   }

    ////////////////////////////////////////////////////////ATTTENDANCE SECTION END////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
