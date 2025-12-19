package com.wms.service;

import com.wms.dto.LocationRequest;
import com.wms.dto.LocationResponse;
import com.wms.entity.Location;
import com.wms.exception.DuplicateResourceException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.LocationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public LocationResponse create(LocationRequest request) {
        // Check for duplicate code
        if (locationRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Location with code '" + request.getCode() + "' already exists");
        }

        Location location = new Location();
        location.setCode(request.getCode());
        location.setZone(request.getZone());
        location.setDescription(request.getDescription());
        location.setActive(request.getActive() != null ? request.getActive() : true);

        Location saved = locationRepository.save(location);
        return LocationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public LocationResponse findById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        return LocationResponse.fromEntity(location);
    }

    @Transactional(readOnly = true)
    public Page<LocationResponse> search(String query, Pageable pageable) {
        return locationRepository.search(query, pageable)
                .map(LocationResponse::fromEntity);
    }

    public LocationResponse update(Long id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));

        // Code is immutable, only update other fields
        location.setZone(request.getZone());
        location.setDescription(request.getDescription());
        if (request.getActive() != null) {
            location.setActive(request.getActive());
        }

        Location saved = locationRepository.save(location);
        return LocationResponse.fromEntity(saved);
    }

    public void softDelete(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
        location.setActive(false);
        locationRepository.save(location);
    }
}
