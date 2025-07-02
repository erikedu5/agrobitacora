package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class EngineerService {

    private final UserRepository userRepository;
    private final CropRepository cropRepository;

    private static final String ROLE_PRODUCTOR = "Productor";

    public List<UserEntity> getProducers() {
        return userRepository.findByRoleName(ROLE_PRODUCTOR);
    }

    public Page<CropEntity> getCrops(Long producerId, CropFilter filter) {
        Pageable paging = PageRequest.of(filter.getPage(), filter.getSize());
        return cropRepository.findAllByUserId(producerId, paging);
    }
}
