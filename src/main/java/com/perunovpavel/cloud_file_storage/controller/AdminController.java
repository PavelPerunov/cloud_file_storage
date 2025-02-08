package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        adminService.delete(email);
        return ResponseEntity.ok("User delete successfully");

    }

    @PutMapping("/users/{email}/role")
    public ResponseEntity<String> updateRole(@PathVariable String email,
                                             @RequestParam String roleName) {
        adminService.updateUserRole(email, roleName);
        return ResponseEntity.ok("User role updated successfully");
    }
}
