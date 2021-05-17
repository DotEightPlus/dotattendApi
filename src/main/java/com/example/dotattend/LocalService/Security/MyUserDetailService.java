package com.example.dotattend.LocalService.Security;

import com.example.dotattend.LocalService.Repository.User;
import com.example.dotattend.LocalService.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user=userRepository.findByMatricNo(username);
        user.orElseThrow(()->new UsernameNotFoundException("Bad credentials"));
        System.out.println("Matric No: "+user.get().getMatricno());
        System.out.println("Role: "+user.get().getRole());
        return user.map(MyUserDetails::new).get();
    }
}
