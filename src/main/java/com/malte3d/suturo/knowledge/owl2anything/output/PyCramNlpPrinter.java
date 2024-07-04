package com.malte3d.suturo.knowledge.owl2anything.output;

import com.google.gson.Gson;
import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import com.malte3d.suturo.knowledge.owl2anything.input.NlpMappingParser;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Utility class to create a .py file for the RoboKudo - PyCRAM - KnowRob interface
 */
@Slf4j
@UtilityClass
public class PyCramNlpPrinter {

    private static final Gson GSON = new Gson().newBuilder()
            .enableComplexMapKeySerialization()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void print(@NonNull List<OwlRecord> classes, @NonNull File outputFile, @NonNull File nlpMappingFile) {

        Map<String, List<String>> mappings = NlpMappingParser.parseNlpMappings(nlpMappingFile);

        TreeMap<String,String> nlpNames = new TreeMap<>();

        for(var record : classes) {
            String iriName = record.getIriName();
            String iri = record.getIriLongForm();

            List<String> mapping = mappings.remove(iriName);

            // If one object type has no nlp mapping, it is assumed that the nlp name is the IRI suffix in lowercase with spaces between words, and a warning will be given.
            if (mapping == null) {
                String fallbackName = OwlRecordConverter.toFallbackNlpFormat(record);
                log.warn("Class {} has no nlp mapping, assuming it is mapped to {}", iriName, fallbackName);
                mapping = List.of(fallbackName);
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
        // TODO Any mapping that is not the suffix of an IRI and does not start with an underscore `_` will cause warning and will be mapped to the root class
        for(var entry : mappings.entrySet()) {
            if(entry.getValue().isEmpty())
                continue;
            log.warn("nlp mapping from '{}' to {} has no knowledge class", entry.getKey(), entry.getValue());
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {

            writer.append("mapping = ").append(GSON.toJson(nlpNames));

            log.info("Successfully created {}", outputFile.getName());

        } catch (IOException e) {
            log.error("Error while writing the Cram Object Names file", e);
        }
    }
}
