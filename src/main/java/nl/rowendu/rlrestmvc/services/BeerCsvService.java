package nl.rowendu.rlrestmvc.services;

import java.io.File;
import java.util.List;
import nl.rowendu.rlrestmvc.model.BeerCsvRecord;

public interface BeerCsvService {
    List<BeerCsvRecord> convertCsv(File csvFile);
}
