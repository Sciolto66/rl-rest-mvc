package nl.rowendu.rlrestmvc.repositories;

import java.util.UUID;
import nl.rowendu.rlrestmvc.entities.BeerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerOrderRepository extends JpaRepository<BeerOrder, UUID> {}
