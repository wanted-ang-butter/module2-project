package com.wanted.naeil.domain.admin.controller;

import com.wanted.naeil.domain.admin.dto.response.UserResponse;
import com.wanted.naeil.domain.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/User")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserResponse> getUser(){
        return  adminUserService.getUser();
    }
    @DeleteMapping("/{id}")
    public void deleteUserId(@PathVariable Long id){
        adminUserService.deleteUser(id);
    }
    @PatchMapping ("/{userId}/activate")
    public void activate(@PathVariable Long userId){
        adminUserService.activateUser(userId);
    }
    @PatchMapping ("/{userId}/deactivate")
    public void deactivate(@PathVariable Long userId){
        adminUserService.deactivateUser(userId);
    }
    @PatchMapping("/{userId}/reset-warning")
    public void resetWarning(@PathVariable Long userId) {
        adminUserService.resetWarnigUser(userId);
    }
}
