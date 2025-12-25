package com.getreviews.engagements_service.repository;

import com.getreviews.engagements_service.entity.Engagements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EngagementsRepository extends JpaRepository<Engagements, Long> {
}
