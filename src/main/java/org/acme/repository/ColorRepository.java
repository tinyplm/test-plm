package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import java.util.UUID;
import org.acme.entity.Color;
import org.hibernate.annotations.processing.Find;


public interface ColorRepository extends PanacheRepository.Managed<Color, UUID> {


    @Find
    Color findByName(String name);

}