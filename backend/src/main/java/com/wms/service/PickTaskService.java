package com.wms.service;

import com.wms.dto.HandheldResponse;
import com.wms.dto.PickLineResponse;
import com.wms.dto.PickTaskResponse;
import com.wms.entity.PickLine;
import com.wms.entity.PickLineStatus;
import com.wms.entity.PickTask;
import com.wms.entity.PickTaskStatus;
import com.wms.exception.ResourceNotFoundException;
import com.wms.repository.PickTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@Transactional(readOnly = true)
public class PickTaskService {

    private final PickTaskRepository pickTaskRepository;

    public PickTaskService(PickTaskRepository pickTaskRepository) {
        this.pickTaskRepository = pickTaskRepository;
    }

    public PickTaskResponse findById(Long id) {
        PickTask task = pickTaskRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickTask not found with id: " + id));
        return PickTaskResponse.fromEntity(task);
    }

    public Page<PickTaskResponse> search(PickTaskStatus status, Pageable pageable) {
        return pickTaskRepository.findByStatusFilter(status, pageable)
                .map(PickTaskResponse::fromEntity);
    }

    public HandheldResponse getHandheldSummary(Long id) {
        PickTask task = pickTaskRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("PickTask not found with id: " + id));

        HandheldResponse response = new HandheldResponse();

        // Find next open line (first OPEN line sorted by location code)
        PickLine nextOpen = task.getLines().stream()
                .filter(l -> l.getStatus() == PickLineStatus.OPEN)
                .min(Comparator.comparing(l -> l.getLocation().getCode()))
                .orElse(null);

        if (nextOpen != null) {
            response.setNextOpenLine(PickLineResponse.fromEntity(nextOpen));
        }

        // Calculate progress
        int totalLines = task.getLines().size();
        int doneLines = (int) task.getLines().stream()
                .filter(l -> l.getStatus() == PickLineStatus.DONE)
                .count();
        int totalQtyAssigned = task.getLines().stream()
                .mapToInt(PickLine::getAssignedQty)
                .sum();
        int totalQtyPicked = task.getLines().stream()
                .mapToInt(PickLine::getPickedQty)
                .sum();

        response.setProgress(new HandheldResponse.Progress(
                totalLines, doneLines, totalQtyAssigned, totalQtyPicked));

        return response;
    }
}
