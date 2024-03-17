package nl.rowendu.rlrestmvc.repositories;

import java.util.UUID;
import nl.rowendu.rlrestmvc.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {}
