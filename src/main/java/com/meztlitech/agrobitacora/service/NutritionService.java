package com.meztlitech.agrobitacora.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meztlitech.agrobitacora.dto.NutritionDetailDto;
import com.meztlitech.agrobitacora.dto.NutritionDto;
import com.meztlitech.agrobitacora.dto.NutritionResponse;
import com.meztlitech.agrobitacora.dto.enums.ApplicationType;
import com.meztlitech.agrobitacora.dto.filters.NutritionFilter;
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


    public NutritionResponse create(NutritionDto nutritionDto, Long cropId, String token) {
        Claims claimsToken = cropUtil.validateCropByUser(token, cropId);
        RoleEntity role = objectMapper.convertValue(claimsToken.get("role"), RoleEntity.class);
        Long userId = Long.parseLong(claimsToken.get("user_id").toString());

        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setApplicationType(ApplicationType.valueOf(nutritionDto.getApplicationType()));
        applicationEntity.setDetail(nutritionDto.getDetail());
        if (role.getName().equals(ROLE_PRODUCTOR)) {
            applicationEntity.setApplicationDate(nutritionDto.getApplicationDate());
        } else if (role.getName().equals(ROLE_INGENIERO)){
            applicationEntity.setVisitDate(nutritionDto.getVisitDate());
        }
        applicationEntity.setUser(userRepository.findById(userId).orElseThrow());
        applicationEntity.setCrop(cropRepository.findById(cropId).orElseThrow());
        ApplicationEntity saved = applicationRepository.save(applicationEntity);

        List<ApplicationDetailEntity> detailEntities = new ArrayList<>();
        nutritionDto.getAppDetails().forEach(appDetail -> {
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

    public Page<NutritionResponse> getAll(NutritionFilter nutritionFilter, Long cropId, String token) {
        cropUtil.validateCropByUser(token, cropId);
        Pageable paging = PageRequest.of(nutritionFilter.getPage(), nutritionFilter.getSize());
        Page<ApplicationEntity> pageApp = applicationRepository.findAllByApplicationType(ApplicationType.NUTRICION, cropId, paging);
        List<NutritionResponse> content = new ArrayList<>();
        pageApp.get().forEach(nutrition -> content.add(this.mapNutrition(nutrition, new ArrayList<>())));

        return new PageImpl<>(content, pageApp.getPageable(), pageApp.getTotalElements());
    }

    public NutritionResponse mapNutrition(ApplicationEntity application, List<ApplicationDetailEntity> detailEntities) {
        NutritionResponse response = new NutritionResponse();
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
        List<NutritionDetailDto> detailDtos = new ArrayList<>();
        detailEntities.forEach(det -> {
            NutritionDetailDto dto = new NutritionDetailDto();
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

    public NutritionResponse findById(long id) {
        ApplicationEntity application = applicationRepository.findById(id).orElseThrow();
        List<ApplicationDetailEntity> applicationDetails = applicationDetailRepository.findAllByApplicationId(application.getId());
        return this.mapNutrition(application, applicationDetails);
    }
}
