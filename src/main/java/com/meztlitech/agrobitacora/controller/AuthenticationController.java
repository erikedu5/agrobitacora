package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.*;
import com.meztlitech.agrobitacora.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signIn")
    public ResponseEntity<UserResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserResponse> signup(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(authenticationService.create(userDto));
    }

    @PostMapping("/verifySession")
    public ResponseEntity<UserResponse> verify(@RequestHeader(value = "Authorization") final String token) {
        return ResponseEntity.ok(authenticationService.verify(token));
    }

    @PutMapping("/changePassword/{id}")
    public ResponseEntity<ActionStatusResponse> update(@PathVariable(name = "id") final long id,
                                                       @RequestBody UserDto userDto) {
        return ResponseEntity.ok(authenticationService.change_password(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionStatusResponse> delete(@PathVariable(name = "id") final long id) {
        return ResponseEntity.ok(authenticationService.delete(id));
    }
}
