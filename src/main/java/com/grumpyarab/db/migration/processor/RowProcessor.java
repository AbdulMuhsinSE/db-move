package com.grumpyarab.db.migration.processor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.opencsv.CSVReader;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
@RequiredArgsConstructor
public class RowProcessor implements ItemProcessor<HashMap<String, Object>, String> {
    final private String url;
    final private String basePath;

    @Override
    public String process(final HashMap<String, Object> source) throws Exception {
        log.info("Pulling from base path: " + basePath + url + ".csv");
        Path path = Paths.get(basePath , url+".csv");
        CSVReader csvReader = new CSVReader(Files.newBufferedReader(path));
        List<String[]> mapping = csvReader.readAll();

        HashMap<String, Object> destination = new HashMap<>(source.size());

        for (int i = 1; i < mapping.size(); i++) {
            destination.put(mapping.get(i)[1], source.get(mapping.get(i)[0]));
        }

        return "INSERT INTO " + mapping.get(0)[1] + "(" + String.join(", ", destination.keySet()) + ") VALUES ("
            +  prepareValues(destination.values()) + ")";
    }

    private String prepareValues(Collection<Object> values) {
        final List<String> valuesString = new ArrayList<>();
        values.forEach( value -> {
            try {
                if(value != null) {
                    Number number = NumberFormat.getInstance().parse(String.valueOf(value));
                }
                valuesString.add(String.valueOf(value));
            } catch (ParseException e) {
                valuesString.add("'" + value.toString().replace("'","''") + "'");
            }
        });
        return String.join(",", valuesString );
    }
}
