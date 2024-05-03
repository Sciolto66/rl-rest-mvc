package nl.rowendu.rlrestmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.entities.Category;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CategoryRepositoryTest {
  @Autowired CategoryRepository categoryRepository;
  @Autowired BeerRepository beerRepository;

  Beer testBeer;

  @BeforeEach
  void setUp() {
    testBeer = beerRepository.findAll().getFirst();
  }

  @Test
  void testGetBeerListByStyle() {
    Page<Beer> list = beerRepository.findAllByBeerStyle(BeerStyle.IPA, null);
    assertThat(list).hasSize(548);
  }

  @Transactional
  @Test
  void testAddCategory() {
    Category savedCategory =
        categoryRepository.save(Category.builder().description("Ales").build());

    //        categoryRepository.flush();
    testBeer.addCategory(savedCategory);
    Beer savedBeer = beerRepository.save(testBeer);

    System.out.println(savedBeer.getBeerName());

    assertThat(savedCategory).isNotNull();
    assertThat(savedCategory.getId()).isNotNull();
  }
}
