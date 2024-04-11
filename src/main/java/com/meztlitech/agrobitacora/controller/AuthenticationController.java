package com.meztlitech.agrobitacora.controller;

import com.meztlitech.agrobitacora.dto.ActionStatusResponse;
import com.meztlitech.agrobitacora.dto.JwtAuthenticationResponse;
import com.meztlitech.agrobitacora.dto.SignInRequest;
import com.meztlitech.agrobitacora.dto.UserDto;
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
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @PostMapping("/signUp")
    public ResponseEntity<UserDto> signup(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(authenticationService.create(userDto));
    }

    @PutMapping("/changePassword/{id}")
    public ResponseEntity<ActionStatusResponse> update(@PathVariable(name = "id") final long id,
                                                       @RequestBody UserDto userDto) {
        return ResponseEntity.ok(authenticationService.change_password(id, userDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActionStatusResponse> update(@PathVariable(name = "id") final long id) {
        return ResponseEntity.ok(authenticationService.delete(id));
    }
}
