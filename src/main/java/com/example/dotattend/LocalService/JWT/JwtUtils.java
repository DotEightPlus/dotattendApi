package com.example.dotattend.LocalService.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtils {
    private static String SECRET_KEY="dottattend,dotattend";

    public String getUsername(String token){
        return getClaim(token, Claims::getSubject);
    }
    public Date getExpiration(String token){
        return getClaim(token,Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date());
    }
    private <T> T getClaim(String token, Function<Claims, T> claimResolver){
        Claims claims=getAllClaim(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaim(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }


    public String generateToken(UserDetails userDetails){
        Map<String,Object> claim=new HashMap<>();
        return createToken(userDetails,claim);
    }

    private String createToken(UserDetails userDetails, Map<String, Object> claim) {
        return Jwts.builder().addClaims(claim).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*24))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY).compact();
    }
    public boolean validateToken(UserDetails userDetails,String token){
        return (userDetails.getUsername().equals(getUsername(token))&&!isTokenExpired(token));
    }
}
