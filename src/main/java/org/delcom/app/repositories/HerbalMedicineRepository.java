package org.delcom.app.repositories;

import org.delcom.app.entities.HerbalMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HerbalMedicineRepository extends JpaRepository<HerbalMedicine, UUID> {
    List<HerbalMedicine> findByUserId(UUID userId);
    
    @Query("SELECT h.category, COUNT(h) FROM HerbalMedicine h GROUP BY h.category")
    List<Object[]> countByCategory();
    
    @Query("SELECT h.origin, COUNT(h) FROM HerbalMedicine h GROUP BY h.origin")
    List<Object[]> countByOrigin();
}

