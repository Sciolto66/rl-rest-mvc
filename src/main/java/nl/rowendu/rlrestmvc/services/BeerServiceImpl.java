package nl.rowendu.rlrestmvc.services;

import lombok.extern.slf4j.Slf4j;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private Map<UUID, BeerDto> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();

        BeerDto beerDto1 = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        BeerDto beerDto2 = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Crank")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356222")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(392)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        BeerDto beerDto3 = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Sunshine City")
                .beerStyle(BeerStyle.IPA)
                .upc("12356")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(144)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        beerMap.put(beerDto1.getId(), beerDto1);
        beerMap.put(beerDto2.getId(), beerDto2);
        beerMap.put(beerDto3.getId(), beerDto3);
    }

    @Override
    public List<BeerDto> listBeers() {
        return new ArrayList<>(beerMap.values());
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID id) {

        log.debug("Get Beer by Id - in service. Id: " + id.toString());

        return Optional.of(beerMap.get(id));
    }

    @Override
    public BeerDto saveNewBeer(BeerDto beerDto) {
        BeerDto savedBeerDto = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .beerName(beerDto.getBeerName())
                .beerStyle(beerDto.getBeerStyle())
                .quantityOnHand(beerDto.getQuantityOnHand())
                .upc(beerDto.getUpc())
                .price(beerDto.getPrice())
                .build();

        beerMap.put(savedBeerDto.getId(), savedBeerDto);
        return savedBeerDto;
    }

    @Override
    public void updateBeerById(UUID beerId, BeerDto beerDto) {
        BeerDto existingBeerDto = beerMap.get(beerId);
        existingBeerDto.setBeerName(beerDto.getBeerName());
        existingBeerDto.setBeerStyle(beerDto.getBeerStyle());
        existingBeerDto.setPrice(beerDto.getPrice());
        existingBeerDto.setQuantityOnHand(beerDto.getQuantityOnHand());
        existingBeerDto.setUpc(beerDto.getUpc());
        existingBeerDto.setUpdateDate(LocalDateTime.now());
    }

    @Override
    public void deleteBeerById(UUID beerId) {
        beerMap.remove(beerId);
    }

    @Override
    public void patchBeerById(UUID beerId, BeerDto beerDto) {
        BeerDto existingBeerDto = beerMap.get(beerId);
        boolean isUpdated = false;

        if (StringUtils.hasText(beerDto.getBeerName())) {
            existingBeerDto.setBeerName(beerDto.getBeerName());
            isUpdated = true;
        }
        if (beerDto.getBeerStyle() != null) {
            existingBeerDto.setBeerStyle(beerDto.getBeerStyle());
            isUpdated = true;
        }
        if (beerDto.getPrice() != null) {
            existingBeerDto.setPrice(beerDto.getPrice());
            isUpdated = true;
        }
        if (beerDto.getQuantityOnHand() != null) {
            existingBeerDto.setQuantityOnHand(beerDto.getQuantityOnHand());
            isUpdated = true;
        }
        if (StringUtils.hasText(beerDto.getUpc())) {
            existingBeerDto.setUpc(beerDto.getUpc());
            isUpdated = true;
        }

        if (isUpdated) {
            existingBeerDto.setUpdateDate(LocalDateTime.now());
        }
    }
}
