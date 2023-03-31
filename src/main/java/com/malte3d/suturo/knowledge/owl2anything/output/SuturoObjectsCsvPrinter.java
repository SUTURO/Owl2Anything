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

@Slf4j
@UtilityClass
public class SuturoObjectsCsvPrinter {

    private static final String[] CSV_HEADER = {
            "IRI Name",
            "IRI ShortForm",
            "IRI Namespace",
            "Description",
            "Perception ID"
    };

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setDelimiter(';')
            .setHeader(CSV_HEADER)
            .build();

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        try (CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8), CSV_FORMAT)) {

            for (OwlRecord owlRecord : classes) {

                csvPrinter.printRecord(
                        owlRecord.getPerceptionId(),
                        owlRecord.getIriName(),
                        owlRecord.getIriShortForm(),
                        owlRecord.getIriNamespace(),
                        owlRecord.getDescription()
                );
            }
        } catch (IOException e) {
           log.error("Error while writing the Suturo Objects CSV file", e);
        }
    }
}
