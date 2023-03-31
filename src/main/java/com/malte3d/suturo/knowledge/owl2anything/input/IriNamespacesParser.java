package com.malte3d.suturo.knowledge.owl2anything.input;

import java.io.File;
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

/**
 * A utility class for parsing a CSV file containing IRIs and their corresponding namespaces shortform and returning a Map of IRI to IRI namespace shortform.
 */
@Slf4j
@Value
public class IriNamespacesParser {

    private static final String[] CSV_HEADER = {"iri", "namespace"};

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader(CSV_HEADER).build();

    /**
     * Reads a CSV file containing IRIs and their corresponding namespaces and returns a Map of the IRIs and namespaces.
     *
     * @param iriNamespacesFile The path of the CSV file containing IRIs and namespaces.
     * @return A Map of the IRIs and namespaces.
     * @throws IllegalArgumentException If the specified file is invalid or cannot be read.
     */
    public static Map<String, String> getIriNamespaces(@NonNull File iriNamespacesFile) {

        try {

            InputStreamReader fileReader = new InputStreamReader(new FileInputStream(iriNamespacesFile), StandardCharsets.UTF_8);

            return CSV_FORMAT.parse(fileReader).getRecords().stream().collect(Collectors.toMap(csvRecord -> csvRecord.get(CSV_HEADER[0]), csvRecord -> csvRecord.get(CSV_HEADER[1])));

        } catch (FileNotFoundException e) {
            log.error("IRI namespaces file not found: {}", iriNamespacesFile.getPath());
        } catch (IOException e) {
            log.error("Error reading IRI namespaces file: {}", iriNamespacesFile.getPath());
        }

        throw new IllegalArgumentException("Invalid IRI namespaces file: " + iriNamespacesFile.getPath());
    }
}
