package com.geolite.projects.repository;

import com.geolite.projects.model.ProjectReadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectReadModelRepository extends JpaRepository<ProjectReadModel, UUID> {

}
