package nl.rowendu.rlrestmvc.services;

import nl.rowendu.rlrestmvc.model.Beer;

import java.util.List;
import java.util.UUID;

public interface BeerService {

    List<Beer> listBeers();

    Beer getBeerById(UUID id);
}
