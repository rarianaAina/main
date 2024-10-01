package com.example.controller;

import com.example.annotation.GET;
import com.example.annotation.Param;
import com.example.session.MySession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyController {

    @GET("/hello")
    public String sayHello(HttpServletRequest request) {
        return "Hello, World!";
    }

    @GET("/welcome")
    public String welcome(@Param(name = "name") String name) {
        return "Welcome " + name;
    }

    @GET("/submit")
    public String submitForm(@Param(name = "name") String name, HttpServletRequest request) {
        return "Form submitted. Name: " + name;
    }

    @GET("/login")
    public String login(@Param(name = "username") String username, MySession session) {
        session.add("user", username);
        return "Logged in as: " + username;
    }

    @GET("/logout")
    public String logout(MySession session) {
        session.delete("user");
        return "Logged out.";
    }

    @GET("/api/user")
    public void getUserData(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().write("{\"name\": \"John Doe\", \"age\": 30}");
    }
}
