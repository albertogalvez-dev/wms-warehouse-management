package com.wms.service;

import com.wms.dto.AssignStationRequest;
import com.wms.dto.ToteResponse;
import com.wms.entity.*;
import com.wms.exception.InvalidOperationException;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.PackingStationRepository;
import com.wms.repository.PickTaskRepository;
import com.wms.repository.ToteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ToteService {

    private final ToteRepository toteRepository;
    private final PackingStationRepository packingStationRepository;
    private final PickTaskRepository pickTaskRepository;

    public ToteService(ToteRepository toteRepository,
            PackingStationRepository packingStationRepository,
            PickTaskRepository pickTaskRepository) {
        this.toteRepository = toteRepository;
        this.packingStationRepository = packingStationRepository;
        this.pickTaskRepository = pickTaskRepository;
    }

    @Transactional(readOnly = true)
    public ToteResponse findByBarcode(String barcode) {
        Tote tote = toteRepository.findByBarcodeWithDetails(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Tote not found with barcode: " + barcode));

        // Calculate picking summary from PickTask lines
        int totalAssigned = 0;
        int totalPicked = 0;

        var pickTask = pickTaskRepository.findByOrderId(tote.getOrder().getId());
        if (pickTask.isPresent()) {
            var task = pickTask.get();
            for (var line : task.getLines()) {
                totalAssigned += line.getAssignedQty();
                totalPicked += line.getPickedQty();
            }
        }

        return ToteResponse.fromEntity(tote, totalAssigned, totalPicked);
    }

    public ToteResponse assignStation(String barcode, AssignStationRequest request) {
        Tote tote = toteRepository.findByBarcodeWithDetails(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Tote not found with barcode: " + barcode));

        // Check tote is OPEN
        if (tote.getStatus() != ToteStatus.OPEN) {
            throw new InvalidOperationException(
                    "Cannot assign station: tote status is " + tote.getStatus() + ", expected OPEN");
        }

        // Check wave is started (IN_PROGRESS or DONE)
        if (tote.getWave().getStatus() != PickWaveStatus.IN_PROGRESS &&
                tote.getWave().getStatus() != PickWaveStatus.DONE) {
            throw new InvalidOperationException(
                    "Cannot assign station: wave status is " + tote.getWave().getStatus() +
                            ", expected IN_PROGRESS or DONE");
        }

        // Find and validate packing station
        PackingStation station = packingStationRepository.findById(request.getStationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Packing station not found with id: " + request.getStationId()));

        if (!station.getActive()) {
            throw new InvalidOperationException(
                    "Cannot assign to inactive packing station: " + station.getCode());
        }

        tote.setPackingStation(station);
        tote.setStatus(ToteStatus.AT_PACKING);
        toteRepository.save(tote);

        return findByBarcode(barcode);
    }

    public ToteResponse close(String barcode) {
        Tote tote = toteRepository.findByBarcodeWithDetails(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Tote not found with barcode: " + barcode));

        // Check order is PICKED
        if (tote.getOrder().getStatus() != OrderStatus.PICKED) {
            throw new InvalidOperationException(
                    "Cannot close tote: order status is " + tote.getOrder().getStatus() +
                            ", expected PICKED");
        }

        tote.setStatus(ToteStatus.CLOSED);
        toteRepository.save(tote);

        return findByBarcode(barcode);
    }
}
