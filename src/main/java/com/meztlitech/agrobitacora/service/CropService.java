package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.CropDto;
import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class CropService {

    private final CropRepository cropRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public CropEntity create(CropDto cropDto, String token) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("id").toString());

        CropEntity cropEntity = new CropEntity();
        cropEntity.setAlias(cropDto.getAlias());
        cropEntity.setLatitud(cropDto.getLatitud());
        cropEntity.setLongitud(cropDto.getLongitud());
        cropEntity.setLocation(cropDto.getLocation());
        cropEntity.setArea(cropDto.getArea());
        cropEntity.setFlowerName(cropDto.getFlowerName());
        cropEntity.setNumberPlants(cropDto.getNumberPlants());
        cropEntity.setUser(userRepository.findById(userId).get());
        return cropRepository.save(cropEntity);
    }

    public Page<CropEntity> getAll(CropFilter cropFilter, String token) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("id").toString());
        Pageable paging = PageRequest.of(cropFilter.getPage(), cropFilter.getSize());
        return cropRepository.findAllByUserId(userId, paging);
    }
}
