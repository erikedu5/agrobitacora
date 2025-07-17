package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.UserDto;
import com.meztlitech.agrobitacora.dto.UserResponse;
import com.meztlitech.agrobitacora.dto.admin.AdminCountsDto;
import com.meztlitech.agrobitacora.dto.ActionStatusResponse;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.UserRepository;
import com.meztlitech.agrobitacora.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationService authenticationService;


    public List<UserEntity> getUsers(String role) {
        if (role != null) {
            return userRepository.findByRoleName(role);
        }
        return userRepository.findAll();
    }

    public UserResponse createUser(UserDto userDto) {
        return authenticationService.create(userDto);
    }

    public ActionStatusResponse changePassword(Long id, UserDto userDto) {
        return authenticationService.change_password(id, userDto);
    }

    public ActionStatusResponse deleteUser(Long id) {
        return authenticationService.delete(id);
    }

    public ActionStatusResponse updateUser(Long id, UserDto userDto) {
        return authenticationService.update(id, userDto);
    }

    public ActionStatusResponse setCropLimit(Long userId, Integer maxCrops) {
        ActionStatusResponse resp = new ActionStatusResponse();
        UserEntity user = userRepository.findById(userId).orElseThrow();
        user.setMaxCrops(maxCrops);
        userRepository.save(user);
        resp.setId(user.getId());
        resp.setStatus(org.springframework.http.HttpStatus.OK);
        resp.setDescription("Actualizado correctamente");
        return resp;
    }

    public UserResponse createEngineer(UserDto userDto) {
        userDto.setRoleId(roleRepository.findByName("Ingeniero").getId());
        return authenticationService.create(userDto);
    }

    public UserResponse createAdmin(UserDto userDto) {
        userDto.setRoleId(roleRepository.findByName("Admin").getId());
        return authenticationService.create(userDto);
    }

    public AdminCountsDto getCounts() {
        AdminCountsDto dto = new AdminCountsDto();
        dto.setProducers(userRepository.countByRoleName("Productor"));
        dto.setEngineers(userRepository.countByRoleName("Ingeniero"));
        dto.setAdmins(userRepository.countByRoleName("Admin"));
        return dto;
    }
}
