package com.malte3d.suturo.knowledge.owl2anything.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to create a txt file for the CRAM object list
 */
@Slf4j
@UtilityClass
public class CramObjectListPrinter {

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        List<OwlRecord> perceptionClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append(generateCramObjectListString(perceptionClasses));

        } catch (IOException e) {
            log.error("Error while writing the Cram Object Names file", e);
        }
    }

    public static String generateCramObjectListString(@NonNull List<OwlRecord> classes) {


        List<OwlRecord> perceptionClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        StringBuilder sb = new StringBuilder("names: [");

        for (int i = 0; i < perceptionClasses.size(); i++) {

            OwlRecord owlRecord = perceptionClasses.get(i);

            sb.append("\"").append(owlRecord.getIriShortForm()).append("\"");

            if (i < perceptionClasses.size() - 1)
                sb.append(", ");
        }

        sb.append("]");

        return sb.toString();
    }
}