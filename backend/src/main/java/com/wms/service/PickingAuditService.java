package com.wms.service;

import com.wms.entity.PickingScanEvent;
import com.wms.entity.PickingScanEventType;
import com.wms.entity.PickingSession;
import com.wms.repository.PickingScanEventRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PickingAuditService {

    private final PickingScanEventRepository pickingScanEventRepository;
    private final EntityManager entityManager;

    public PickingAuditService(PickingScanEventRepository pickingScanEventRepository, EntityManager entityManager) {
        this.pickingScanEventRepository = pickingScanEventRepository;
        this.entityManager = entityManager;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long sessionId, String scannedCode, PickingScanEventType eventType, String message) {
        PickingScanEvent event = new PickingScanEvent();
        event.setSession(entityManager.getReference(PickingSession.class, sessionId));
        event.setScannedCode(scannedCode);
        event.setEventType(eventType);
        event.setMessage(message);
        pickingScanEventRepository.save(event);
    }
}

