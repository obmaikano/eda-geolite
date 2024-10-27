package com.geolite.scenarios.repository;

import com.geolite.scenarios.model.ScenarioReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScenarioReadModelRepository extends JpaRepository<ScenarioReadModel, UUID> {
    List<ScenarioReadModel> findByProjectId(UUID projectId);
}
