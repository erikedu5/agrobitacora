package com.meztlitech.agrobitacora.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meztlitech.agrobitacora.dto.ApplicationDetailDto;
import com.meztlitech.agrobitacora.dto.ApplicationDto;
import com.meztlitech.agrobitacora.dto.ApplicationResponse;
import com.meztlitech.agrobitacora.dto.enums.ApplicationType;
import com.meztlitech.agrobitacora.dto.filters.ApplicationFilter;
import com.meztlitech.agrobitacora.entity.ApplicationDetailEntity;
import com.meztlitech.agrobitacora.entity.ApplicationEntity;
import com.meztlitech.agrobitacora.entity.RoleEntity;
import com.meztlitech.agrobitacora.repository.ApplicationDetailRepository;
import com.meztlitech.agrobitacora.repository.ApplicationRepository;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import com.meztlitech.agrobitacora.util.CropUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class NutritionService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationDetailRepository applicationDetailRepository;
    private final CropUtil cropUtil;
    private final ObjectMapper objectMapper;
    private final CropRepository cropRepository;
    private final UserRepository userRepository;

    private static final String ROLE_PRODUCTOR = "Productor";
    private static final String ROLE_INGENIERO = "Ingeniero";


    public ApplicationResponse create(ApplicationDto applicationDto, Long cropId, String token) {
        Claims claimsToken = cropUtil.validateCropByUser(token, cropId);
        RoleEntity role = objectMapper.convertValue(claimsToken.get("role"), RoleEntity.class);
        Long userId = Long.parseLong(claimsToken.get("id").toString());

        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setApplicationType(ApplicationType.NUTRICION);
        applicationEntity.setDetail(applicationDto.getDetail());
        if (role.getName().equals(ROLE_PRODUCTOR)) {
            applicationEntity.setApplicationDate(applicationDto.getApplicationDate());
        } else if (role.getName().equals(ROLE_INGENIERO)){
            applicationEntity.setVisitDate(applicationDto.getVisitDate());
        }
        applicationEntity.setUser(userRepository.findById(userId).orElseThrow());
        applicationEntity.setCrop(cropRepository.findById(cropId).orElseThrow());
        ApplicationEntity saved = applicationRepository.save(applicationEntity);

        List<ApplicationDetailEntity> detailEntities = new ArrayList<>();
        applicationDto.getAppDetails().forEach(appDetail -> {
            ApplicationDetailEntity applicationDetailEntity = new ApplicationDetailEntity();
            applicationDetailEntity.setCondiciones(appDetail.getCondiciones());
            applicationDetailEntity.setDosis(appDetail.getDosis());
            applicationDetailEntity.setUnit(appDetail.getUnit());
            applicationDetailEntity.setActiveIngredient(appDetail.getActiveIngredient());
            applicationDetailEntity.setProductName(appDetail.getProductName());
            applicationDetailEntity.setApplication(saved);
            detailEntities.add(applicationDetailRepository.save(applicationDetailEntity));
        });

        return this.mapNutrition(saved, detailEntities);
    }

    public Page<ApplicationResponse> getAll(ApplicationFilter applicationFilter, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        Pageable paging = PageRequest.of(applicationFilter.getPage(), applicationFilter.getSize());
        Page<ApplicationEntity> pageApp = applicationRepository.findAllByApplicationType(ApplicationType.NUTRICION, cropId, paging);
        List<ApplicationResponse> content = new ArrayList<>();
        pageApp.get().forEach(app -> {
            List<ApplicationDetailEntity> details = applicationDetailRepository.findAllByApplicationId(app.getId());
            content.add(this.mapNutrition(app, details));
        });

        return new PageImpl<>(content, pageApp.getPageable(), pageApp.getTotalElements());
    }

    public ApplicationResponse mapNutrition(ApplicationEntity application, List<ApplicationDetailEntity> detailEntities) {
        ApplicationResponse response = new ApplicationResponse();
        response.setDetail(application.getDetail());
        if (application.getUser().getRole().getName().equals(ROLE_INGENIERO)) {
            response.setTechnicalName(application.getUser().getName());
            response.setRecommendationType("TECHNICAL");
        } else {
            response.setRecommendationType("AUTO");
        }
        response.setApplicationType(application.getApplicationType().toString());
        response.setApplicationDate(application.getApplicationDate());
        response.setVisitDate(application.getVisitDate());
        response.setId(application.getId());
        List<ApplicationDetailDto> detailDtos = new ArrayList<>();
        detailEntities.forEach(det -> {
            ApplicationDetailDto dto = new ApplicationDetailDto();
            dto.setCondiciones(det.getCondiciones());
            dto.setDosis(det.getDosis());
            dto.setProductName(det.getProductName());
            dto.setUnit(det.getUnit());
            dto.setActiveIngredient(det.getActiveIngredient());
            detailDtos.add(dto);
        });
        response.setAppDetails(detailDtos);
        return response;
    }

    public ApplicationResponse findById(long id) {
        ApplicationEntity application = applicationRepository.findById(id).orElseThrow();
        List<ApplicationDetailEntity> applicationDetails = applicationDetailRepository.findAllByApplicationId(application.getId());
        return this.mapNutrition(application, applicationDetails);
    }

    public ApplicationResponse update(Long id, ApplicationDto applicationDto, String token) {
        ApplicationEntity application = applicationRepository.findById(id).orElseThrow();
        cropUtil.validateCropByUser(token, application.getCrop().getId());
        application.setDetail(applicationDto.getDetail());
        application.setApplicationDate(applicationDto.getApplicationDate());
        application.setVisitDate(applicationDto.getVisitDate());
        applicationDetailRepository.deleteAll(applicationDetailRepository.findAllByApplicationId(id));
        List<ApplicationDetailEntity> detailEntities = new ArrayList<>();
        applicationDto.getAppDetails().forEach(det -> {
            ApplicationDetailEntity entity = new ApplicationDetailEntity();
            entity.setCondiciones(det.getCondiciones());
            entity.setDosis(det.getDosis());
            entity.setUnit(det.getUnit());
            entity.setActiveIngredient(det.getActiveIngredient());
            entity.setProductName(det.getProductName());
            entity.setApplication(application);
            detailEntities.add(applicationDetailRepository.save(entity));
        });
        ApplicationEntity saved = applicationRepository.save(application);
        return this.mapNutrition(saved, detailEntities);
    }

    public void delete(Long id, String token) {
        ApplicationEntity application = applicationRepository.findById(id).orElseThrow();
        cropUtil.validateCropByUser(token, application.getCrop().getId());
        applicationDetailRepository.deleteAll(applicationDetailRepository.findAllByApplicationId(id));
        applicationRepository.delete(application);
    }
}
