package com.wms.repository;

import com.wms.entity.PickingScanEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickingScanEventRepository extends JpaRepository<PickingScanEvent, Long> {
}

