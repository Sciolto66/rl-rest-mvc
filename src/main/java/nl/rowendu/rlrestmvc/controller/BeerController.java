package nl.rowendu.rlrestmvc.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import nl.rowendu.rlrestmvc.services.BeerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${beer.api.path}")
public class BeerController {

  private final BeerService beerService;

  @Value("${beer.api.path}")
  private String beerPath;

  @PatchMapping("/{beerId}")
  public ResponseEntity<BeerDto> patchBeerById(@PathVariable("beerId") UUID beerId,
                                               @RequestBody BeerDto beerDto) {
    if (beerService.patchBeerById(beerId, beerDto).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{beerId}")
  public ResponseEntity<Void> deleteBeerById(@PathVariable("beerId") UUID beerId) {
    if (!beerService.deleteBeerById(beerId)) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/{beerId}")
  public ResponseEntity<BeerDto> updateBeerById(@PathVariable("beerId") UUID beerId,
                                                @Validated @RequestBody BeerDto beerDto) {
    if (beerService.updateBeerById(beerId, beerDto).isEmpty()) {
      throw new NotFoundException();
    }
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping()
  public ResponseEntity<BeerDto> handlePost(@Validated @RequestBody BeerDto beerDto) {
    BeerDto savedBeerDto = beerService.saveNewBeer(beerDto);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Location", beerPath + "/" + savedBeerDto.getId().toString());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @GetMapping()
  public Page<BeerDto> listBeers(@RequestParam(required = false) String beerName,
                                 @RequestParam(required = false) BeerStyle beerStyle,
                                 @RequestParam(required = false) Boolean showInventory,
                                 @RequestParam(required = false) Integer pageNumber,
                                 @RequestParam(required = false) Integer pageSize) {
    return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
  }

  @GetMapping("/{beerId}")
  public BeerDto getBeerById(@PathVariable("beerId") UUID beerId) {
    return beerService.getBeerById(beerId)
            .orElseThrow(NotFoundException::new);
  }
}
