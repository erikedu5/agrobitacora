package com.meztlitech.agrobitacora.service;

import com.meztlitech.agrobitacora.dto.filters.CropFilter;
import com.meztlitech.agrobitacora.entity.CropEntity;
import com.meztlitech.agrobitacora.entity.UserEntity;
import com.meztlitech.agrobitacora.repository.CropRepository;
import com.meztlitech.agrobitacora.repository.EngineerProducerRepository;
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
    private final EngineerProducerRepository engineerProducerRepository;

    private static final String ROLE_PRODUCTOR = "Productor";
    private static final String ROLE_INGENIERO = "Ingeniero";

    public List<UserEntity> getAllProducers() {
        return userRepository.findByRoleName(ROLE_PRODUCTOR);
    }

    public List<UserEntity> getAllEngineers() {
        return userRepository.findByRoleName(ROLE_INGENIERO);
    }

    public List<UserEntity> getProducers(Long engineerId) {
        return engineerProducerRepository.findByEngineerId(engineerId).stream()
                .map(ep -> ep.getProducer())
                .toList();
    }

    public List<UserEntity> getEngineers(Long producerId) {
        return engineerProducerRepository.findByProducerId(producerId).stream()
                .map(ep -> ep.getEngineer())
                .toList();
    }

    public void setEngineerProducers(Long engineerId, List<Long> producerIds) {
        engineerProducerRepository.findByEngineerId(engineerId)
                .forEach(engineerProducerRepository::delete);
        UserEntity engineer = userRepository.findById(engineerId).orElseThrow();
        for (Long pid : producerIds) {
            UserEntity producer = userRepository.findById(pid).orElseThrow();
            EngineerProducerEntity link = new EngineerProducerEntity();
            link.setEngineer(engineer);
            link.setProducer(producer);
            engineerProducerRepository.save(link);
        }
    }

    public void setProducerEngineers(Long producerId, List<Long> engineerIds) {
        engineerProducerRepository.findByProducerId(producerId)
                .forEach(engineerProducerRepository::delete);
        UserEntity producer = userRepository.findById(producerId).orElseThrow();
        for (Long eid : engineerIds) {
            UserEntity engineer = userRepository.findById(eid).orElseThrow();
            EngineerProducerEntity link = new EngineerProducerEntity();
            link.setEngineer(engineer);
            link.setProducer(producer);
            engineerProducerRepository.save(link);
        }
    }

    public Page<CropEntity> getCrops(Long producerId, CropFilter filter) {
        Pageable paging = PageRequest.of(filter.getPage(), filter.getSize());
        return cropRepository.findAllByUserId(producerId, paging);
    }
}
