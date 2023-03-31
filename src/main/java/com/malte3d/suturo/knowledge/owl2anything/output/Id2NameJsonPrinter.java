package com.malte3d.suturo.knowledge.owl2anything.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class Id2NameJsonPrinter {

    private static final Gson GSON = new Gson().newBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();


    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        TreeMap<Integer, String> perceptionNames = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .collect(Collectors.toMap(OwlRecord::getPerceptionId, OwlRecord::getIriShortForm,
                        (c1, c2) -> {
                            throw new IllegalStateException(String.format("Duplicate perceptionIds for classes %s and %s", c1, c2));
                        },
                        TreeMap::new));

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append(GSON.toJson(perceptionNames));

        } catch (IOException e) {
            log.error("Error while writing the Id2Name JSON file", e);
        }
    }

}
