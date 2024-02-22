package nl.rowendu.rlrestmvc.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import nl.rowendu.rlrestmvc.model.BeerCsvRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

class BeerCsvServiceImplTest {

  BeerCsvService beerCsvService = new BeerCsvServiceImpl();

  @Test
  void convertCSV() throws FileNotFoundException {

    File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

    List<BeerCsvRecord> recs = beerCsvService.convertCsv(file);

    System.out.println(recs.size());

    assertThat(recs.size()).isPositive();
  }
}
