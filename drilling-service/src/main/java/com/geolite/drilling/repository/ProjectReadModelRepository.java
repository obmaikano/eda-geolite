package com.geolite.drilling.repository;

import com.geolite.drilling.model.ProjectReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectReadModelRepository extends JpaRepository<ProjectReadModel, UUID> {
}
