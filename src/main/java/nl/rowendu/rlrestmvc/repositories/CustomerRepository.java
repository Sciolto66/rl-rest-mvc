package nl.rowendu.rlrestmvc.repositories;

import nl.rowendu.rlrestmvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {}
