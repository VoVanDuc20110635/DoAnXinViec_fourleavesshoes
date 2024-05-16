package com.data.filtro.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccessDenied {
    @GetMapping("/access-denied")
    public String getAccessDenied(HttpServletResponse response){
        System.out.println("bạn đã truy cập vào trang access-denied");
        return "error/accessDenied";
    }
}
