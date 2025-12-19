package com.wms.repository;

import com.wms.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByCode(String code);

    @Query("SELECT l FROM Location l WHERE " +
            "(:query IS NULL OR :query = '' OR " +
            "LOWER(l.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(l.zone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Location> search(@Param("query") String query, Pageable pageable);

    boolean existsByCode(String code);
}
