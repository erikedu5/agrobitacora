package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.IrrigationDto;
import com.meztlitech.agrobitacora.dto.enums.IrrigationType;
import com.meztlitech.agrobitacora.dto.filters.IrrigationFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.IrrigationEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.IrrigationRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class IrrigationService {

    private final IrrigationRepository irrigationRepository;
    private final CropRepository cropRepository;
    private final JwtService jwtService;

    public Page<IrrigationEntity> getAll(IrrigationFilter irrigationFilter, Long cropId, String token) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("user_id").toString());
        if (!cropRepository.findByIdAndUserId(cropId, userId).isPresent()) {
            throw new HttpServerErrorException(HttpStatus.PRECONDITION_FAILED, "The Crop can't be use by this user");
        }
        Pageable paging = PageRequest.of(irrigationFilter.getPage(), irrigationFilter.getSize());
        return irrigationRepository.findAllByCropId(cropId, paging);
    }

    public IrrigationEntity create(IrrigationDto irrigationDto, Long cropId, String token) {
        Claims claims = jwtService.decodeToken(token);
        Long userId = Long.parseLong(claims.get("user_id").toString());
        Optional<CropEntity> crop = cropRepository.findByIdAndUserId(cropId, userId);
        if (!crop.isPresent()) {
            throw new HttpServerErrorException(HttpStatus.PRECONDITION_FAILED, "The Crop can't be use by this user");
        }
        IrrigationEntity irrigationEntity = new IrrigationEntity();
        irrigationEntity.setType(IrrigationType.valueOf(irrigationDto.getType()));
        irrigationEntity.setTimeInIrrigationInMinutes(irrigationDto.getTimeInIrrigationInMinutes());
        irrigationEntity.setCrop(crop.get());
        irrigationEntity.setDate(irrigationDto.getDate());
        irrigationEntity.setConditions(irrigationDto.getConditions());

        return irrigationRepository.save(irrigationEntity);
    }
}
