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
import com.malte3d.suturo.knowledge.owl2anything.converter.Size;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to create a suturo_objects_default_sizes.csv
 *
 * <p>
 * It only outputs the classes that have a perception id.
 * </p>
 */
@Slf4j
@UtilityClass
public class SuturoObjectsDefaultSizeCsvPrinter {

    private static final String[] CSV_HEADER = {
            "IRI",
            "Width",
            "Height",
            "Depth",
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setHeader(CSV_HEADER)
            .build();

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        List<OwlRecord> perceptionClasses = PerceptionClassesFilter.filter(classes);

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8), CSV_FORMAT)) {

            for (OwlRecord owlRecord : perceptionClasses) {

                Size defaultSize = owlRecord.getDefaultSize();

                csvPrinter.printRecord(
                        owlRecord.getIriShortForm(),
                        defaultSize != null ? defaultSize.getWidth() : null,
                        defaultSize != null ? defaultSize.getHeight() : null,
                        defaultSize != null ? defaultSize.getDepth() : null
                );
            }

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the Suturo Objects Default Sizes CSV file", e);
        }
    }
}
