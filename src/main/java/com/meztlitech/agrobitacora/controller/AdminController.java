package com.meztlitech.agrobitacora.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meztlitech.agrobitacora.dto.CropDto;
import com.meztlitech.agrobitacora.dto.UserDto;
import com.meztlitech.agrobitacora.dto.UserResponse;
import com.meztlitech.agrobitacora.dto.ActionStatusResponse;
import com.meztlitech.agrobitacora.dto.admin.AdminCountsDto;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.RoleEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.service.AdminService;
import com.meztlitech.agrobitacora.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final AdminService adminService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String ROLE_ADMIN = "Administrador";

    private void validateAdmin(String token) {
        Claims claims = jwtService.decodeToken(token);
        RoleEntity role = objectMapper.convertValue(claims.get("role"), RoleEntity.class);
        if (!ROLE_ADMIN.equals(role.getName())) {
            throw new HttpServerErrorException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    @PostMapping("/users/{userId}/crops")
    public ResponseEntity<CropEntity> createCrop(@PathVariable Long userId,
                                                 @Valid @RequestBody CropDto cropDto,
                                                 @RequestHeader(value = "Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(adminService.createCropForUser(userId, cropDto));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> users(@RequestHeader(value = "Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserDto userDto,
                                                   @RequestHeader(value = "Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(adminService.createUser(userDto));
    }

    @PutMapping("/users/{id}/password")
    public ResponseEntity<ActionStatusResponse> changePassword(@PathVariable Long id,
                                                               @RequestBody UserDto userDto,
                                                               @RequestHeader(value = "Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(adminService.changePassword(id, userDto));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ActionStatusResponse> deleteUser(@PathVariable Long id,
                                                           @RequestHeader(value = "Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    @GetMapping("/counts")
    public ResponseEntity<AdminCountsDto> counts(@RequestHeader(value = "Authorization") String token) {
        validateAdmin(token);
        return ResponseEntity.ok(adminService.getCounts());
    }
}
