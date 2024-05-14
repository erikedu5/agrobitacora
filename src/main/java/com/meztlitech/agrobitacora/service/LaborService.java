package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.LaborDto;
import com.meztlitech.agrobitacora.dto.filters.LaborFilter;
import com.meztlitech.agrobitacora.entity.LaborEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.LaborRepository;
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
public class LaborService {

    private final LaborRepository laborRepository;
    private final CropRepository cropRepository;
    private final CropUtil cropUtil;

    public LaborEntity create(LaborDto laborDto, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        LaborEntity laborEntity = new LaborEntity();
        laborEntity.setKindLabor(laborDto.getKindLabor());
        laborEntity.setLaborDate(laborDto.getLaborDate());
        laborEntity.setTimeLabor(laborDto.getTimeLabor());
        laborEntity.setDescription(laborDto.getDescription());
        laborEntity.setWorkers_number(laborDto.getWorkers_number());
        laborEntity.setCrop(cropRepository.findById(cropId).orElseThrow());
        return laborRepository.save(laborEntity);
    }

    public Page<LaborEntity> getAll(LaborFilter laborFilter, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        Pageable paging = PageRequest.of(laborFilter.getPage(), laborFilter.getSize());
        return laborRepository.findAllByCropId(cropId, paging);
    }
}
