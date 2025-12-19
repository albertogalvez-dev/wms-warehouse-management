package com.wms.service;

import com.wms.dto.PackingStationRequest;
import com.wms.dto.PackingStationResponse;
import com.wms.entity.PackingStation;
import com.wms.exception.DuplicateResourceException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.PackingStationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PackingStationService {

    private final PackingStationRepository packingStationRepository;

    public PackingStationService(PackingStationRepository packingStationRepository) {
        this.packingStationRepository = packingStationRepository;
    }

    public PackingStationResponse create(PackingStationRequest request) {
        if (packingStationRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                    "Packing station with code '" + request.getCode() + "' already exists");
        }

        PackingStation station = new PackingStation();
        station.setCode(request.getCode());
        station.setName(request.getName());
        station.setActive(request.getActive() != null ? request.getActive() : true);

        PackingStation saved = packingStationRepository.save(station);
        return PackingStationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PackingStationResponse findById(Long id) {
        PackingStation station = packingStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Packing station not found with id: " + id));
        return PackingStationResponse.fromEntity(station);
    }

    @Transactional(readOnly = true)
    public List<PackingStationResponse> findAllActive() {
        return packingStationRepository.findByActiveTrue().stream()
                .map(PackingStationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PackingStationResponse update(Long id, PackingStationRequest request) {
        PackingStation station = packingStationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Packing station not found with id: " + id));

        // Check code uniqueness if changed
        if (!station.getCode().equals(request.getCode()) &&
                packingStationRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                    "Packing station with code '" + request.getCode() + "' already exists");
        }

        station.setCode(request.getCode());
        station.setName(request.getName());
        if (request.getActive() != null) {
            station.setActive(request.getActive());
        }

        PackingStation saved = packingStationRepository.save(station);
        return PackingStationResponse.fromEntity(saved);
    }
}
