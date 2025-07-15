package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.UserRepository;
import com.meztlitech.agrobitacora.repository.EngineerProducerRepository;
import com.meztlitech.agrobitacora.repository.EngineerReviewRepository;
import com.meztlitech.agrobitacora.service.JwtService;
import com.meztlitech.agrobitacora.entity.EngineerProducerEntity;
import com.meztlitech.agrobitacora.entity.EngineerReviewEntity;
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
    private final EngineerReviewRepository engineerReviewRepository;
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

    public List<UserEntity> getAvailableProducers(String token) {
        Claims claims = jwtService.decodeToken(token);
        Long engineerId = Long.parseLong(claims.get("id").toString());
        List<UserEntity> all = getAllProducers();
        List<UserEntity> mine = engineerProducerRepository.findProducersByEngineerId(engineerId);
        all.removeAll(mine);
        return all;
    }

    public List<UserEntity> getEngineers(String token) {
        Claims claims = jwtService.decodeToken(token);
        Long producerId = Long.parseLong(claims.get("id").toString());
        List<UserEntity> list = engineerProducerRepository.findEngineersByProducerId(producerId);
        list.forEach(e -> e.setRanking(getAverageRanking(e.getId())));
        list.sort((a,b) -> Double.compare(b.getRanking()==null?0:b.getRanking(), a.getRanking()==null?0:a.getRanking()));
        return list;
    }

    public List<UserEntity> getAllEngineers() {
        List<UserEntity> list = userRepository.findByRoleName("Ingeniero");
        list.forEach(e -> e.setRanking(getAverageRanking(e.getId())));
        list.sort((a,b) -> Double.compare(b.getRanking()==null?0:b.getRanking(), a.getRanking()==null?0:a.getRanking()));
        return list;
    }

    public List<UserEntity> getAvailableEngineers(String token) {
        Claims claims = jwtService.decodeToken(token);
        Long producerId = Long.parseLong(claims.get("id").toString());
        List<UserEntity> all = getAllEngineers();
        List<UserEntity> mine = engineerProducerRepository.findEngineersByProducerId(producerId);
        all.removeAll(mine);
        return all;
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

    private double getAverageRanking(Long engineerId) {
        Double avg = engineerReviewRepository.averageRatingByEngineerId(engineerId);
        return avg == null ? 0.0 : avg;
    }

    public void rateEngineer(String token, Long engineerId, int rating, String review) {
        Claims claims = jwtService.decodeToken(token);
        Long producerId = Long.parseLong(claims.get("id").toString());
        EngineerReviewEntity entity = engineerReviewRepository
                .findByEngineerIdAndProducerId(engineerId, producerId)
                .orElseGet(() -> EngineerReviewEntity.builder()
                        .engineer(userRepository.findById(engineerId).orElseThrow())
                        .producer(userRepository.findById(producerId).orElseThrow())
                        .build());
        entity.setRating(rating);
        entity.setReview(review);
        engineerReviewRepository.save(entity);
    }
}
