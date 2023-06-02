package com.malte3d.suturo.knowledge.owl2anything.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to create a txt file for the CRAM - KnowRob interface
 */
@Slf4j
@UtilityClass
public class CramKnowRobPrinter {

    private static final Pattern SINGLE_QUOTE = Pattern.compile("'");

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        List<OwlRecord> perceptionClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append(generateCramObjectListString(perceptionClasses));

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the Cram Object Names file", e);
        }
    }

    public static String generateCramObjectListString(@NonNull List<OwlRecord> classes) {


        List<OwlRecord> perceptionClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        StringBuilder sb = new StringBuilder();

        for (OwlRecord owlRecord : perceptionClasses) {

            sb.append("\t").append("(:").append(OwlRecordConverter.toCramFormat(owlRecord)).append("\n");
            sb.append("\t").append(" \"").append(OwlRecordConverter.toKnowRobFormat(owlRecord)).append("\"").append(")").append("\n");
        }

        return sb.toString();
    }

    private static String escapeSingleQuotes(String literal) {
        return SINGLE_QUOTE.matcher(literal).replaceAll("\\\\'");
    }
}
