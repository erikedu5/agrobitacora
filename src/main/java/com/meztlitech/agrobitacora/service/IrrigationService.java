package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.IrrigationDto;
import com.meztlitech.agrobitacora.dto.CatalogDto;
import com.meztlitech.agrobitacora.dto.enums.IrrigationType;
import com.meztlitech.agrobitacora.dto.filters.IrrigationFilter;
import com.meztlitech.agrobitacora.entity.IrrigationEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.IrrigationRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class IrrigationService {

    private final IrrigationRepository irrigationRepository;
    private final CropUtil cropUtil;
    private final CropRepository cropRepository;

    public Page<IrrigationEntity> getAll(IrrigationFilter irrigationFilter, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        Pageable paging = PageRequest.of(irrigationFilter.getPage(), irrigationFilter.getSize());
        return irrigationRepository.findAllByCropId(cropId, paging);
    }

    public IrrigationEntity create(IrrigationDto irrigationDto, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        IrrigationEntity irrigationEntity = new IrrigationEntity();
        irrigationEntity.setType(IrrigationType.valueOf(irrigationDto.getType()));
        irrigationEntity.setTimeInIrrigationInMinutes(irrigationDto.getTimeInIrrigationInMinutes());
        irrigationEntity.setCrop(cropRepository.findById(cropId).orElseThrow());
        irrigationEntity.setDate(irrigationDto.getDate());
        irrigationEntity.setConditions(irrigationDto.getConditions());

        return irrigationRepository.save(irrigationEntity);
    }

    public IrrigationEntity update(Long id, IrrigationDto irrigationDto, String token) {
        IrrigationEntity irrigation = irrigationRepository.findById(id).orElseThrow();
        cropUtil.validateCropByUser(token, irrigation.getCrop().getId());
        irrigation.setType(IrrigationType.valueOf(irrigationDto.getType()));
        irrigation.setTimeInIrrigationInMinutes(irrigationDto.getTimeInIrrigationInMinutes());
        irrigation.setDate(irrigationDto.getDate());
        irrigation.setConditions(irrigationDto.getConditions());
        return irrigationRepository.save(irrigation);
    }

    public void delete(Long id, String token) {
        IrrigationEntity irrigation = irrigationRepository.findById(id).orElseThrow();
        cropUtil.validateCropByUser(token, irrigation.getCrop().getId());
        irrigationRepository.delete(irrigation);
    }

    public java.util.List<CatalogDto> catalog(Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        java.util.List<IrrigationEntity> irrigations = irrigationRepository.findAllListByCropId(cropId);
        java.util.List<CatalogDto> list = new java.util.ArrayList<>();
        irrigations.forEach(ir -> {
            CatalogDto dto = new CatalogDto();
            dto.setId(ir.getId());
            dto.setDescription(ir.getType() + " " + ir.getDate());
            list.add(dto);
        });
        return list;
    }
}
