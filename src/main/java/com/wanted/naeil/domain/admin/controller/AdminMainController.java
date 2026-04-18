package com.wanted.naeil.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminMainController {

    @GetMapping("/user-management")
    public String userList() {
        return "admin/user-list";
    }

    @GetMapping("/blacklist")
    public String blacklist() {
        return "admin/blacklist";
    }

    @GetMapping("/category-management")
    public String category() {
        return "admin/category";
    }

    @GetMapping("/course-management/approval")
    public String courseApproval() {
        return "admin/AdminApproval";
    }

    @GetMapping("/instructor-approval")
    public String instructorApproval() {
        return "admin/instructor-application";
    }

    @GetMapping("/live-management")
    public String liveApporval() {
        return "admin/live-approval";
    }

    @GetMapping("/settlement")
    public  String settlement() {
        return "admin/settlement";
    }

}
