package com.malte3d.suturo.knowledge.owl2anything.input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.checkerframework.checker.units.qual.N;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class IriNamespacesParser {

    private static final String[] CSV_HEADER = {
            "iri",
            "namespace"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setHeader(CSV_HEADER)
            .build();

    public Map<String, String> getIriNamespaces(@NonNull String iriNamespacesFile) {

        try {

            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(iriNamespacesFile), StandardCharsets.UTF_8);

            return CSV_FORMAT.parse(fileReader).getRecords()
                    .stream()
                    .collect(Collectors.toMap(csvRecord -> csvRecord.get(CSV_HEADER[0]), csvRecord -> csvRecord.get(CSV_HEADER[1])));

        } catch (FileNotFoundException e) {
            log.error("IRI namespaces file not found: {}", iriNamespacesFile);
        } catch (IOException e) {
            log.error("Error reading IRI namespaces file: {}", iriNamespacesFile);
        }

        throw new IllegalArgumentException("Invalid IRI namespaces file: " + iriNamespacesFile);
    }
}
