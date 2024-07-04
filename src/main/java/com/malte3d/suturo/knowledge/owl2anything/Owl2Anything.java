package com.malte3d.suturo.knowledge.owl2anything;

import java.io.File;
import java.util.List;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlConverter;
import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import com.malte3d.suturo.knowledge.owl2anything.input.StarterArgs;
import com.malte3d.suturo.knowledge.owl2anything.output.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * The {@link Owl2Anything} class provides a main method that extracts data from an OWL ontology file and outputs it in various formats such as CSV, JSON, and
 * text files.
 */
@Slf4j
@UtilityClass
public class Owl2Anything {

    private static final String OUTPUT_DIR = "owl2anything/output";

    public static void main(String[] args) {

        StarterArgs starterArgs = new StarterArgs(args);

        OwlConverter owlConverter = OwlConverter.builder()
                .ontologyFile(starterArgs.getOntologyFile())
                .iriRoot(starterArgs.getIriRoot())
                .iriMappingFile(starterArgs.getIriMappingFile())
                .iriNamespacesFile(starterArgs.getIriNamespacesFile())
                .build();

        List<OwlRecord> owlRecords = owlConverter.extractRecords();

        log.info("Extracted {} OwlRecords", owlRecords.size());

        File outputDir = new File(OUTPUT_DIR);
        outputDir.mkdirs();

        SuturoObjectsCsvPrinter.print(owlRecords, new File(outputDir, "suturo_objects.csv"));
        // SuturoObjectsDefaultSizeCsvPrinter.print(owlRecords, new File(outputDir, "suturo_objects_default_sizes.csv"));
        Id2NameJsonPrinter.print(owlRecords, new File(outputDir, "id2name.json"));
        YoloObjNamesPrinter.print(owlRecords, new File(outputDir, "obj.names"));
        CramRoboKudoPrinter.print(owlRecords, new File(outputDir, "cram_robokudo.txt"));
        CramKnowRobPrinter.print(owlRecords, new File(outputDir, "cram_knowrob.txt"));
        PyCramKnowRobPrinter.print(owlRecords, new File(outputDir, "pycram_knowrob.py"));
		PyCramRoboKudoPrinter.print(owlRecords, new File(outputDir, "pycram_robokudo.py"));
        
        if (starterArgs.getNlpMappingFile().isEmpty())
            log.info("No nlp mappings file given, so no pycram_nlp.py will be generated.");
        else
            PyCramNlpPrinter.print(owlRecords, new File(outputDir, "pycram_nlp.py"), starterArgs.getNlpMappingFile().get());
    }
}
