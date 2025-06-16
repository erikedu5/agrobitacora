package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.UserDto;
import com.meztlitech.agrobitacora.dto.UserResponse;
import com.meztlitech.agrobitacora.dto.admin.AdminCountsDto;
import com.meztlitech.agrobitacora.dto.ActionStatusResponse;
import com.meztlitech.agrobitacora.dto.CropDto;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final CropRepository cropRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    public CropEntity createCropForUser(Long userId, CropDto cropDto) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        CropEntity crop = new CropEntity();
        crop.setAlias(cropDto.getAlias());
        crop.setLatitud(cropDto.getLatitud());
        crop.setLongitud(cropDto.getLongitud());
        crop.setLocation(cropDto.getLocation());
        crop.setArea(cropDto.getArea());
        crop.setFlowerName(cropDto.getFlowerName());
        crop.setNumberPlants(cropDto.getNumberPlants());
        crop.setUser(user);
        return cropRepository.save(crop);
    }

    public List<UserEntity> getUsers() {
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

    public AdminCountsDto getCounts() {
        AdminCountsDto dto = new AdminCountsDto();
        dto.setUsers(userRepository.count());
        dto.setCrops(cropRepository.count());
        return dto;
    }
}
