package nl.rowendu.rlrestmvc.services;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGE_SIZE = 25;
  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  public Page<BeerDto> listBeers(
      String beerName,
      BeerStyle beerStyle,
      Boolean showInventory,
      Integer pageNumber,
      Integer pageSize) {

    PageRequest pageRequest = buildPageRequest(pageNumber, pageSize);
    Page<Beer> beerPage;

    if (StringUtils.hasText(beerName) && beerStyle == null) {
      beerPage = listBeersByName(beerName, pageRequest);
    } else if (!StringUtils.hasText(beerName) && beerStyle != null) {
      beerPage = listBeersByStyle(beerStyle, pageRequest);
    } else if (StringUtils.hasText(beerName) && beerStyle != null) {
      beerPage = listBeersByNameAndStyle(beerName, beerStyle, pageRequest);
    } else {
      beerPage = beerRepository.findAll(pageRequest);
    }

    if (showInventory != null && !showInventory) {
      beerPage.forEach(beer -> beer.setQuantityOnHand(null));
    }

    return beerPage.map(beerMapper::beerToBeerDto);
  }

  public PageRequest buildPageRequest(Integer pageNumber, Integer pageSize) {
    int queryPageNumber;
    int queryPageSize;

    if (pageNumber != null && pageNumber > 0) {
      queryPageNumber = pageNumber - 1;
    } else {
      queryPageNumber = DEFAULT_PAGE;
    }

    if (pageSize == null) {
      queryPageSize = DEFAULT_PAGE_SIZE;
    } else {
      if (pageSize > 1000) {
        queryPageSize = 1000;
      } else {
        queryPageSize = pageSize;
      }
    }

    Sort sort = Sort.by(Sort.Order.asc("beerName"));

    return PageRequest.of(queryPageNumber, queryPageSize, sort);
  }

  private Page<Beer> listBeersByNameAndStyle(String beerName, BeerStyle beerStyle, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(
        "%" + beerName + "%", beerStyle, pageable);
  }

  public Page<Beer> listBeersByStyle(BeerStyle beerStyle, Pageable pageable) {
    return beerRepository.findAllByBeerStyle(beerStyle, pageable);
  }

  public Page<Beer> listBeersByName(String beerName, Pageable pageable) {
    return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName + "%", pageable);
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
