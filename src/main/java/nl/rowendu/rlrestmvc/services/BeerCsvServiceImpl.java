package nl.rowendu.rlrestmvc.services;

import com.opencsv.bean.CsvToBeanBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import nl.rowendu.rlrestmvc.model.BeerCsvRecord;
import org.springframework.stereotype.Service;

@Service
public class BeerCsvServiceImpl implements BeerCsvService {
    @Override
    public List<BeerCsvRecord> convertCsv(File csvFile) {

        try {
            return new CsvToBeanBuilder<BeerCsvRecord>(new FileReader(csvFile))
                    .withType(BeerCsvRecord.class)
                    .build().parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
