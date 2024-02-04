package nl.rowendu.rlrestmvc.services;

import nl.rowendu.rlrestmvc.model.BeerDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDto> listBeers();

    Optional<BeerDto> getBeerById(UUID id);

    BeerDto saveNewBeer(BeerDto beerDto);

    Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beerDto);

    void deleteBeerById(UUID beerId);

    void patchBeerById(java.util.UUID beerId, BeerDto beerDto);
}
