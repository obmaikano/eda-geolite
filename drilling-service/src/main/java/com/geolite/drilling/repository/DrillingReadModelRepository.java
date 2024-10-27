package com.geolite.drilling.repository;

import com.geolite.drilling.model.DrillingReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrillingReadModelRepository extends JpaRepository<DrillingReadModel, UUID> {
    List<DrillingReadModel> findDrillingReadModelByScenarioId(UUID scenarioId);
}
