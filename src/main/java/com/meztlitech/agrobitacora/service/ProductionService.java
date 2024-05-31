package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.ProductionDetailDto;
import com.meztlitech.agrobitacora.dto.ProductionDto;
import com.meztlitech.agrobitacora.dto.ProductionResponse;
import com.meztlitech.agrobitacora.dto.filters.ProductionFilter;
import com.meztlitech.agrobitacora.entity.ProductionDetailEntity;
import com.meztlitech.agrobitacora.entity.ProductionEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.ProductionDetailRepository;
import com.meztlitech.agrobitacora.repository.ProductionRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final ProductionDetailRepository productionDetailRepository;
    private final CropRepository cropRepository;
    private final CropUtil cropUtil;

    public ProductionEntity create(ProductionDto productionDto, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        ProductionEntity productionEntity = new ProductionEntity();
        productionEntity.setProductionDate(productionDto.getProductionDate());
        productionEntity.setCrop(cropRepository.findById(cropId).orElseThrow());
        ProductionEntity saved = productionRepository.save(productionEntity);
        for (ProductionDetailDto detail: productionDto.getDetailDtoList()) {
            ProductionDetailEntity productionDetailEntity = new ProductionDetailEntity();
            productionDetailEntity.setDescriptionProduct(detail.getDescriptionProduct());
            productionDetailEntity.setProductionNumber(detail.getProductionNumber());
            productionDetailEntity.setAnnotation(detail.getAnnotation());
            productionDetailEntity.setProduction(saved);
            productionDetailRepository.save(productionDetailEntity);
        }
        return saved;
    }

    public Page<ProductionResponse> getAll(ProductionFilter productionFilter, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        Pageable paging = PageRequest.of(productionFilter.getPage(), productionFilter.getSize());
        Page<ProductionEntity> prod = productionRepository.findAllByCropId(cropId, paging);
        List<ProductionResponse> content = new ArrayList<>();
        prod.get().forEach(production -> content.add(this.mapProduction(production)));
        return new PageImpl<>(content, prod.getPageable(), prod.getTotalElements());
    }

    private ProductionResponse mapProduction(ProductionEntity prod) {
        ProductionResponse productionResponse = new ProductionResponse();
        productionResponse.setProductionDate(prod.getProductionDate());
        productionResponse.setId(prod.getId());
        List<ProductionDetailDto> detailDtoList = new ArrayList<>();
        productionDetailRepository.findAllByProductionId(prod.getId()).forEach(detail -> {
            ProductionDetailDto detailDto = new ProductionDetailDto();
            detailDto.setAnnotation(detail.getAnnotation());
            detailDto.setDescriptionProduct(detail.getDescriptionProduct());
            detailDto.setProductionNumber(detail.getProductionNumber());
            detailDtoList.add(detailDto);
        });
        productionResponse.setDetailDtoList(detailDtoList);
        return productionResponse;
    }

    public ProductionResponse findById(long id) {
        ProductionEntity production = productionRepository.findById(id).orElseThrow();
        return this.mapProduction(production);
    }
}
