package com.malte3d.suturo.knowledge.owl2anything.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        List<OwlRecord> perceptionClasses = PerceptionClassesFilter.filter(classes);

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8), CSV_FORMAT)) {

            for (OwlRecord owlRecord : perceptionClasses) {

                csvPrinter.printRecord(OwlRecordConverter.toRoboKudoFormat(owlRecord));
            }

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the YOLO obj.name file", e);
        }
    }

}
