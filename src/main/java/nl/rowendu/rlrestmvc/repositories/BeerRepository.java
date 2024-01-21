package nl.rowendu.rlrestmvc.repositories;

import nl.rowendu.rlrestmvc.entities.Beer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BeerRepository extends JpaRepository<Beer, UUID>  {}
