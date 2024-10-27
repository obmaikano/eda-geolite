package com.geolite.drilling.repository;

import com.geolite.drilling.model.Drilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DrillingRepository extends JpaRepository<Drilling, UUID> {
    List<Drilling> findDrillingsByScenarioId(UUID scenarioId);
}
