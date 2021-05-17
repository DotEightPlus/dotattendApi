package com.example.dotattend.LocalService.Security;

import com.example.dotattend.LocalService.Repository.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {
    private String matricno;
    private String password;
    private List<SimpleGrantedAuthority> authorities;
    private User user;
    public MyUserDetails(User user){
        this.user=user;
        this.matricno=user.getMatricno();
        this.password=user.getPassword();
        this.authorities= Arrays.stream(user.getRole().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return matricno;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //1 signifies that account is notlocked
        //0 signifies that account is locked
        if(user.getAccountstatus()==1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
