package nl.rowendu.rlrestmvc.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.mappers.BeerMapper;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import nl.rowendu.rlrestmvc.repositories.BeerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public List<BeerDto> listBeers(String beerName, BeerStyle beerStyle, Boolean showInventory) {
    List<Beer> beerList;

    if (StringUtils.hasText(beerName) && beerStyle == null) {
      beerList = listBeersByName(beerName);
    } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
      beerList = listBeersByStyle(beerStyle);
    } else if (StringUtils.hasText(beerName) && beerStyle != null) {
      beerList = listBeersByNameAndStyle(beerName, beerStyle);
    } else {
      beerList = beerRepository.findAll();
    }

    if (showInventory != null && !showInventory) {
      beerList.forEach(beer -> beer.setQuantityOnHand(null));
    }

    return beerList.stream().map(beerMapper::beerToBeerDto).toList();
  }

    private List<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%" + beerName + "%", beerStyle);
    }

    public List<Beer> listBeersByStyle(BeerStyle beerStyle) {
        return beerRepository.findAllByBeerStyle(beerStyle);
    }

    public List<Beer> listBeersByName(String beerName){
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%");
    }

  @Override
  public Optional<BeerDto> getBeerById(UUID id) {
    return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(id).orElse(null)));
  }

  @Override
  public BeerDto saveNewBeer(BeerDto beerDto) {
    return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
  }

  @Override
  public Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beerDto) {
    AtomicReference<Optional<BeerDto>> beerDtoOptional = new AtomicReference<>();

    beerRepository
        .findById(beerId)
        .ifPresentOrElse(
            beer -> {
              beer.setBeerName(beerDto.getBeerName());
              beer.setBeerStyle(beerDto.getBeerStyle());
              beer.setUpc(beerDto.getUpc());
              beer.setPrice(beerDto.getPrice());
              beer.setQuantityOnHand(beerDto.getQuantityOnHand());
              beerDtoOptional.set(Optional.of(beerMapper.beerToBeerDto(beerRepository.save(beer))));
            },
            () -> beerDtoOptional.set(Optional.empty()));

    return beerDtoOptional.get();
  }

  @Override
  public boolean deleteBeerById(UUID beerId) {
    if (beerRepository.existsById(beerId)) {
      beerRepository.deleteById(beerId);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Optional<BeerDto> patchBeerById(UUID beerId, BeerDto beerDto) {
    AtomicReference<Optional<BeerDto>> beerDtoOptional = new AtomicReference<>();

    beerRepository
        .findById(beerId)
        .ifPresentOrElse(
            existingBeerDto -> {
              if (StringUtils.hasText(beerDto.getBeerName())) {
                existingBeerDto.setBeerName(beerDto.getBeerName());
              }
              if (beerDto.getBeerStyle() != null) {
                existingBeerDto.setBeerStyle(beerDto.getBeerStyle());
              }
              if (beerDto.getPrice() != null) {
                existingBeerDto.setPrice(beerDto.getPrice());
              }
              if (beerDto.getQuantityOnHand() != null) {
                existingBeerDto.setQuantityOnHand(beerDto.getQuantityOnHand());
              }
              if (StringUtils.hasText(beerDto.getUpc())) {
                existingBeerDto.setUpc(beerDto.getUpc());
              }

              beerDtoOptional.set(
                  Optional.of(beerMapper.beerToBeerDto(beerRepository.save(existingBeerDto))));
            },
            () -> beerDtoOptional.set(Optional.empty()));

    return beerDtoOptional.get();
  }
}
