package com.example.dotattend.LocalService.JWT;

//This model contain the token, role and other user information
public class JwtModel {
    private String role;
    private String token;

    public JwtModel() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
