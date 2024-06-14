package com.malte3d.suturo.knowledge.owl2anything.output;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class to create a .py file for the PyCRAM - KnowRob interface
 */
@Slf4j
@UtilityClass
public class PyCramKnowRobPrinter {

    private static final Pattern SINGLE_QUOTE = Pattern.compile("'");

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        List<OwlRecord> output = PerceptionClassesFilter.filter(classes);

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append(generateCramObjectListString(output));

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

            sb.append(OwlRecordConverter.toPycramFormat(owlRecord))
                    .append(" = '").append(OwlRecordConverter.toKnowRobFormat(owlRecord)).append("'").append("\n");
        }

        return sb.toString();
    }

    private static String escapeSingleQuotes(String literal) {
        return SINGLE_QUOTE.matcher(literal).replaceAll("\\\\'");
    }
}
