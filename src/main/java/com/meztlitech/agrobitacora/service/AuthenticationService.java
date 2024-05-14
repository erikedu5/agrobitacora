package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.ActionStatusResponse;
import com.meztlitech.agrobitacora.dto.JwtAuthenticationResponse;
import com.meztlitech.agrobitacora.dto.SignInRequest;
import com.meztlitech.agrobitacora.dto.UserDto;
import com.meztlitech.agrobitacora.dto.UserResponse;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.RoleRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationService {

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public UserResponse signIn(SignInRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
            UserEntity user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
            String jwt = jwtService.generateToken(user, user.getId());

            UserResponse userDto = new UserResponse();
            userDto.setEmail(user.getUsername());
            userDto.setId(user.getId());
            userDto.setFullName(user.getName());
            userDto.setRole(user.getRole());
            userDto.setToken(JwtAuthenticationResponse.builder().token(jwt).build().getToken());
            return userDto;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

    public UserResponse create(UserDto request) {
        try {
            UserEntity user = new UserEntity();
            if (Objects.nonNull(request.getRoleId())) {
                user.setRole(roleRepository.findById(request.getRoleId()).get());
            } else if (Objects.isNull(user.getRole())) {
                user.setRole(roleRepository.findByIsDefault(true));
            }
            user.setUserName(request.getUserName());
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setActive(true);
            userRepository.save(user);

            return this.signIn(new SignInRequest(request.getUserName(), request.getPassword()));
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return new UserResponse();
    }

    public ActionStatusResponse change_password(long id, UserDto userDto) {
        ActionStatusResponse actionStatusResponse = new ActionStatusResponse();
        try {
            UserEntity user = userRepository.findById(id).orElseThrow();

            if (StringUtils.isNotBlank(userDto.getPassword())) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            UserEntity saved = userRepository.save(user);

            actionStatusResponse.setId(saved.getId());
            actionStatusResponse.setStatus(HttpStatus.OK);
            actionStatusResponse.setDescription("Actualizado correctamente");
        } catch (Exception ex) {
            Map<HttpStatus, String> errors = new HashMap<>();
            errors.put(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            actionStatusResponse.setErrors(errors);
        }
        return actionStatusResponse;
    }

    public ActionStatusResponse delete(long id) {
        ActionStatusResponse actionStatusResponse = new ActionStatusResponse();
        try {
            UserEntity user = userRepository.findById(id).orElseThrow();
            user.setActive(false);
            userRepository.save(user);
            actionStatusResponse.setId(id);
            actionStatusResponse.setStatus(HttpStatus.OK);
            actionStatusResponse.setDescription("Borrado correctamente");
        } catch (Exception ex) {
            Map<HttpStatus, String> errors = new HashMap<>();
            errors.put(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            actionStatusResponse.setErrors(errors);
        }
        return actionStatusResponse;
    }

}
