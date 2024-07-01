package com.malte3d.suturo.knowledge.owl2anything.output;

import com.google.gson.Gson;
import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Utility class to create a .py file for the RoboKudo - PyCRAM - KnowRob interface
 */
@Slf4j
@UtilityClass
public class PyCramRasaPrinter {

    private static final Gson GSON = new Gson().newBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        List<OwlRecord> output = PerceptionClassesFilter.filter(classes);

		TreeMap<String, String> perceptionNames = output.stream()
			.collect(Collectors.toMap(OwlRecordConverter::toRoboKudoFormat, OwlRecordConverter::toKnowRobFormat,
									  (c1, c2) -> {
										  throw new IllegalStateException(String.format("Duplicate robokudo name for classes %s and %s", c1, c2));
									  },
									  TreeMap::new));

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append("mapping = ").append(GSON.toJson(perceptionNames));

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the Cram Object Names file", e);
        }
    }
}
