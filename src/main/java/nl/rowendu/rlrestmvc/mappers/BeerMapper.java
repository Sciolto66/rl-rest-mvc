package nl.rowendu.rlrestmvc.mappers;

import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDto beerDto);
    BeerDto beerToBeerDto(Beer beer);
}
