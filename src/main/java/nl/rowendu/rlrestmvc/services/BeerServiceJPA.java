package nl.rowendu.rlrestmvc.services;

import lombok.RequiredArgsConstructor;
import nl.rowendu.rlrestmvc.mappers.BeerMapper;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.repositories.BeerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDto> listBeers() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::beerToBeerDto)
                .toList();
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID id) {
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(id)
                .orElse(null)));
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDto)));
    }

    @Override
    public Optional<BeerDto> updateBeerById(UUID beerId, BeerDto beerDto) {
        AtomicReference<Optional<BeerDto>> beerDtoOptional = new AtomicReference<>();

        beerRepository.findById(beerId).ifPresentOrElse(beer -> {
            beer.setBeerName(beerDto.getBeerName());
            beer.setBeerStyle(beerDto.getBeerStyle());
            beer.setUpc(beerDto.getUpc());
            beer.setPrice(beerDto.getPrice());
            beer.setQuantityOnHand(beerDto.getQuantityOnHand());
            beerDtoOptional.set(Optional.of(beerMapper.beerToBeerDto(beerRepository.save(beer))));
        }, () -> beerDtoOptional.set(Optional.empty()));

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
    public void patchBeerById(UUID beerId, BeerDto beerDto) {

    }
}
