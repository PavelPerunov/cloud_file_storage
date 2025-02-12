package com.perunovpavel.cloud_file_storage.controller;

import com.perunovpavel.cloud_file_storage.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Operations", description = "Endpoints for administrating users.")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Delete user by email",
            description = "Deletes user with the specified email address."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access Denied (no admin role)")
    })
    @DeleteMapping("/users/{email}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Email of the user to delete")
            @PathVariable String email) {
        adminService.delete(email);
        return ResponseEntity.ok("User delete successfully");

    }

    @Operation(
            summary = "Update user's role",
            description = "Updates the role of user with the specified email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User role updated successfully"),
            @ApiResponse(responseCode = "404", description = "User or role not found"),
            @ApiResponse(responseCode = "403", description = "Access Denied (no admin role)")
    })
    @PutMapping("/users/{email}/role")
    public ResponseEntity<String> updateRole(
            @Parameter(description = "Email of the user")
            @PathVariable String email,
            @Parameter(description = "Role name to assign (e.g. 'ADMIN', 'USER')")
            @RequestParam String roleName) {
        adminService.updateUserRole(email, roleName);
        return ResponseEntity.ok("User role updated successfully");
    }
}
