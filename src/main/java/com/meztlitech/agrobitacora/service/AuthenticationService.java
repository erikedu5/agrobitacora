package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.ActionStatusResponse;
import com.meztlitech.agrobitacora.dto.JwtAuthenticationResponse;
import com.meztlitech.agrobitacora.dto.SignInRequest;
import com.meztlitech.agrobitacora.dto.UserDto;
import com.meztlitech.agrobitacora.dto.UserResponse;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.RoleRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import com.meztlitech.agrobitacora.repository.CropRepository;
import io.jsonwebtoken.Claims;
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

    private final CropRepository cropRepository;

    public UserResponse signIn(SignInRequest request) {
        try {
            UserEntity user = userRepository.findByUserNameOrWhatsapp(request.getLogin())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));
            String jwt = jwtService.generateToken(user, user.getId(), request.isRemember());

            UserResponse userDto = new UserResponse();
            userDto.setEmail(user.getUsername());
            userDto.setWhatsapp(user.getWhatsapp());
            userDto.setId(user.getId());
            userDto.setFullName(user.getName());
            userDto.setRole(user.getRole());
            userDto.setToken(JwtAuthenticationResponse.builder().token(jwt).build().getToken());
            userDto.setMaxCrops(user.getMaxCrops());
            userDto.setCropCount(cropRepository.countByUserId(user.getId()));
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
            user.setUserName(request.getEmail());
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setActive(true);
            user.setWhatsapp(request.getWhatsapp());
            user.setMaxCrops(request.getMaxCrops());
            userRepository.save(user);

            return this.signIn(new SignInRequest(request.getEmail(), request.getPassword(), false));
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

    public ActionStatusResponse update(long id, UserDto userDto) {
        ActionStatusResponse actionStatusResponse = new ActionStatusResponse();
        try {
            UserEntity user = userRepository.findById(id).orElseThrow();
            if (StringUtils.isNotBlank(userDto.getName())) {
                user.setName(userDto.getName());
            }
            if (StringUtils.isNotBlank(userDto.getEmail())) {
                user.setUserName(userDto.getEmail());
            }
            if (StringUtils.isNotBlank(userDto.getPassword())) {
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }
            if (StringUtils.isNotBlank(userDto.getWhatsapp())) {
                user.setWhatsapp(userDto.getWhatsapp());
            }
            if (userDto.getRoleId() != null) {
                user.setRole(roleRepository.findById(userDto.getRoleId()).orElseThrow());
            }
            if (userDto.getMaxCrops() != null) {
                user.setMaxCrops(userDto.getMaxCrops());
            }
            userRepository.save(user);
            actionStatusResponse.setId(user.getId());
            actionStatusResponse.setStatus(HttpStatus.OK);
            actionStatusResponse.setDescription("Actualizado correctamente");
        } catch (Exception ex) {
            Map<HttpStatus, String> errors = new HashMap<>();
            errors.put(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            actionStatusResponse.setErrors(errors);
        }
        return actionStatusResponse;
    }

    public UserResponse verify(String token) {
        Claims claims = jwtService.decodeToken(token);

        UserEntity user = userRepository.findByUserName(claims.get("email").toString())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        UserResponse userDto = new UserResponse();
        userDto.setEmail(user.getUsername());
        userDto.setWhatsapp(user.getWhatsapp());
        userDto.setId(user.getId());
        userDto.setFullName(user.getName());
        userDto.setRole(user.getRole());
        userDto.setToken(token);
        userDto.setMaxCrops(user.getMaxCrops());
        userDto.setCropCount(cropRepository.countByUserId(user.getId()));

        return userDto;
    }

    public UserResponse refreshToken(String token) {
        Claims claims = jwtService.decodeToken(token);
        String email = claims.get("email").toString();
        UserEntity user = userRepository.findByUserName(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));
        String newToken = jwtService.generateToken(user, user.getId());

        UserResponse userDto = new UserResponse();
        userDto.setEmail(user.getUsername());
        userDto.setWhatsapp(user.getWhatsapp());
        userDto.setId(user.getId());
        userDto.setFullName(user.getName());
        userDto.setRole(user.getRole());
        userDto.setToken(newToken);
        userDto.setMaxCrops(user.getMaxCrops());
        userDto.setCropCount(cropRepository.countByUserId(user.getId()));

        return userDto;
    }
}
