package com.malte3d.suturo.knowledge.owl2anything.input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class IriMappingParser {

    private static final String[] CSV_HEADER = {
            "original",
            "replacement"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setHeader(CSV_HEADER)
            .build();

    public Map<String, String> getIriMapping(@NonNull String iriMappingFile) {

        try {

            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(iriMappingFile), StandardCharsets.UTF_8);

            return CSV_FORMAT.parse(fileReader).getRecords()
                    .stream()
                    .collect(Collectors.toMap(csvRecord -> csvRecord.get(CSV_HEADER[0]), csvRecord -> csvRecord.get(CSV_HEADER[1])));

        } catch (FileNotFoundException e) {
            log.error("IRI mapping file not found: {}", iriMappingFile);
        } catch (IOException e) {
            log.error("Error reading IRI mapping file: {}", iriMappingFile);
        }

        throw new IllegalArgumentException("Invalid IRI mapping file: " + iriMappingFile);
    }
}
