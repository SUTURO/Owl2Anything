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

/**
 * Utility class to create a .py file for the Rasa - PyCRAM - KnowRob interface
 */
@Slf4j
@UtilityClass
public class PyCramNlpPrinter {

    private static final Gson GSON = new Gson().newBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile) {

        TreeMap<String,String> nlpNames = new TreeMap<>();

        for(var record : classes) {
            String iri = record.getIriLongForm();

            List<String> mapping = record.getPredefinedNames();

            if (mapping.isEmpty()) {
                // the nlp naming script already generates warnings, and excludes some classes
                // so warning about this would only generate useless noise.
                // log.warn("Class {} has no nlp mapping", iriName);
                continue;
            }

            for(String nlpName : mapping) {
                // In case of two IRI classes that use the same name, a warning will be given and the lexically smaller class IRI will be used.
                String currentIri = nlpNames.get(nlpName);
                if (currentIri == null)
                    nlpNames.put(nlpName, iri);
                else {
                    String taken = iri.compareTo(currentIri) < 0 ? iri : currentIri;
                    log.warn("'{}' and '{}' both map to '{}', using '{}'", currentIri, iri, nlpName, taken);
                    nlpNames.put(nlpName, taken);
                }
            }
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append("mapping = ").append(GSON.toJson(nlpNames));

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the Cram Object Names file", e);
        }
    }
}
