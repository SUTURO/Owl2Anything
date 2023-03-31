package com.malte3d.suturo.knowledge.owl2anything.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to create a YOLO obj.names file for the SUTURO perception group
 */
@Slf4j
@UtilityClass
public class YoloObjNamesPrinter {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .build();

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile)  {

        List<OwlRecord> perceptionClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        validateClasses(perceptionClasses);

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8), CSV_FORMAT)) {

            for (OwlRecord owlRecord : perceptionClasses) {

                csvPrinter.printRecord(
                        owlRecord.getIriShortForm()
                );
            }

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the YOLO obj.name file", e);
        }
    }

    private static void validateClasses(List<OwlRecord> records) {

        checkForIncompleteIds(records);
        checkForDuplicates(records);
    }

    private static void checkForIncompleteIds(List<OwlRecord> records) {

        List<OwlRecord> incompleteRecords = records.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() == null)
                .toList();

        if (!incompleteRecords.isEmpty())
            throw new IllegalStateException("Classes without Id found: " + incompleteRecords);

        boolean isSortedAndContinuous = records.stream()
                .mapToInt(OwlRecord::getPerceptionId)
                .reduce((int a, int b) -> b == (a + 1) ? b : Integer.MIN_VALUE)
                .orElse(Integer.MIN_VALUE) != Integer.MIN_VALUE;

        if (!isSortedAndContinuous)
            throw new IllegalStateException("Ids are not continuously increasing: " + records);
    }

    private static void checkForDuplicates(List<OwlRecord> records) {

        HashSet<Integer> seen = new HashSet<>(records.size());
        HashSet<OwlRecord> duplicate = new HashSet<>();

        for (OwlRecord csvRecord : records)
            if (!seen.add(csvRecord.getPerceptionId()))
                duplicate.add(csvRecord);

        if (!duplicate.isEmpty())
            throw new IllegalStateException("Duplicate perception Ids found: " + duplicate);
    }

}
