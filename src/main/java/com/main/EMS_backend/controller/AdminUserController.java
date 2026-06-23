package com.main.EMS_backend.controller;

import com.main.EMS_backend.dto.UserSummaryResponse;
import com.main.EMS_backend.service.DashboardService;
import com.main.EMS_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Slf4j
public class AdminUserController {
    @Autowired
    private UserService userService;
    @GetMapping("/{role}")
    public ResponseEntity<List<UserSummaryResponse>> getUsers(@PathVariable String role) {
        List<UserSummaryResponse> users = userService.getUsersByRole(role)
                .stream()
                .map(UserSummaryResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("USER DELETED");
    }
    @PutMapping("/demote/{id}")
    public ResponseEntity<?> demote(@PathVariable Long id) {
        userService.changeRole(id);
        return ResponseEntity.ok("ROLE CHANGED");
    }

    @Autowired
    private DashboardService dashboardService;
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
