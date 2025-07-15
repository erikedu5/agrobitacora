package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import com.meztlitech.agrobitacora.repository.EngineerProducerRepository;
import com.meztlitech.agrobitacora.service.JwtService;
import com.meztlitech.agrobitacora.entity.EngineerProducerEntity;
import io.jsonwebtoken.Claims;
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
    private final EngineerProducerRepository engineerProducerRepository;
    private final JwtService jwtService;

    private static final String ROLE_PRODUCTOR = "Productor";

    public List<UserEntity> getProducers(String token) {
        Claims claims = jwtService.decodeToken(token);
        Long engineerId = Long.parseLong(claims.get("id").toString());
        return engineerProducerRepository.findProducersByEngineerId(engineerId);
    }

    public List<UserEntity> getAllProducers() {
        return userRepository.findByRoleName(ROLE_PRODUCTOR);
    }

    public List<UserEntity> getEngineers(String token) {
        Claims claims = jwtService.decodeToken(token);
        Long producerId = Long.parseLong(claims.get("id").toString());
        return engineerProducerRepository.findEngineersByProducerId(producerId);
    }

    public List<UserEntity> getAllEngineers() {
        return userRepository.findByRoleName("Ingeniero");
    }

    public void addClient(String token, Long producerId) {
        Claims claims = jwtService.decodeToken(token);
        Long engineerId = Long.parseLong(claims.get("id").toString());
        if (!engineerProducerRepository.existsByEngineerIdAndProducerId(engineerId, producerId)) {
            EngineerProducerEntity ep = EngineerProducerEntity.builder()
                    .engineer(userRepository.findById(engineerId).orElseThrow())
                    .producer(userRepository.findById(producerId).orElseThrow())
                    .build();
            engineerProducerRepository.save(ep);
        }
    }

    public void removeClient(String token, Long producerId) {
        Claims claims = jwtService.decodeToken(token);
        Long engineerId = Long.parseLong(claims.get("id").toString());
        engineerProducerRepository.deleteByEngineerIdAndProducerId(engineerId, producerId);
    }

    public void addEngineer(String token, Long engineerId) {
        Claims claims = jwtService.decodeToken(token);
        Long producerId = Long.parseLong(claims.get("id").toString());
        if (!engineerProducerRepository.existsByEngineerIdAndProducerId(engineerId, producerId)) {
            EngineerProducerEntity ep = EngineerProducerEntity.builder()
                    .engineer(userRepository.findById(engineerId).orElseThrow())
                    .producer(userRepository.findById(producerId).orElseThrow())
                    .build();
            engineerProducerRepository.save(ep);
        }
    }

    public void removeEngineer(String token, Long engineerId) {
        Claims claims = jwtService.decodeToken(token);
        Long producerId = Long.parseLong(claims.get("id").toString());
        engineerProducerRepository.deleteByEngineerIdAndProducerId(engineerId, producerId);
    }

    public Page<CropEntity> getCrops(Long producerId, CropFilter filter) {
        Pageable paging = PageRequest.of(filter.getPage(), filter.getSize());
        return cropRepository.findAllByUserId(producerId, paging);
    }
}
