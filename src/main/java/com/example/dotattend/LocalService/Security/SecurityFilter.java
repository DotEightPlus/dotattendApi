package com.example.dotattend.LocalService.Security;

import com.example.dotattend.LocalService.CustomException.MyException;
import com.example.dotattend.LocalService.JWT.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class SecurityFilter extends OncePerRequestFilter {
    public static String matricno;
    public static String jwt;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MyUserDetailService myUserDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("[FILTER]--FILTERING-->>>>>> CHECKING REQUEST REQUEST");
        System.out.println(request.getRemoteAddr()+" Making request");
        final String authorizationHeader=request.getHeader("Authorization");
        matricno=null;
        jwt=null;
        if (authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")){
            jwt=authorizationHeader.substring(7);
            matricno=jwtUtils.getUsername(jwt);
        }
        if (matricno!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            //Username is appended with thw comma and school id to make thing go as expected in the userdetails service
            UserDetails userDetails= myUserDetailsService.loadUserByUsername(matricno);
            if (userDetails!=null){
                if (jwtUtils.validateToken(userDetails,jwt)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(
                            userDetails,null,userDetails.getAuthorities()
                    );
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        request.setAttribute("matricno",matricno);
        filterChain.doFilter(request,response);
    }
}
