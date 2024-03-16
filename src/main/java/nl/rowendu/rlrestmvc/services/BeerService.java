package nl.rowendu.rlrestmvc.services;

import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.model.BeerStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDto> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory);

    Optional<BeerDto> getBeerById(UUID id);

    BeerDto saveNewBeer(BeerDto beerDto);

    Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beerDto);

    boolean deleteBeerById(UUID beerId);

    Optional<BeerDto> patchBeerById(UUID beerId, BeerDto beerDto);
}
