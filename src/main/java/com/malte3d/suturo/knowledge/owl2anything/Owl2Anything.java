package com.malte3d.suturo.knowledge.owl2anything;

import java.io.File;
import java.util.List;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlConverter;
import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import com.malte3d.suturo.knowledge.owl2anything.input.StarterArgs;
import com.malte3d.suturo.knowledge.owl2anything.output.CramObjectListPrinter;
import com.malte3d.suturo.knowledge.owl2anything.output.Id2NameJsonPrinter;
import com.malte3d.suturo.knowledge.owl2anything.output.SuturoObjectsCsvPrinter;
import com.malte3d.suturo.knowledge.owl2anything.output.YoloObjNamesPrinter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class Owl2Anything {

    public static void main(String[] args) {

        StarterArgs starterArgs = new StarterArgs(args);


        OwlConverter owlConverter = OwlConverter.builder()
                .ontologyFilePath(starterArgs.getOntologyFile())
                .iriRoot(starterArgs.getIriRoot())
                .iriMappingFile(starterArgs.getIriMappingFile())
                .iriNamespacesFile(starterArgs.getIriNamespacesFile())
                .build();

        List<OwlRecord> owlRecords = owlConverter.extractRecords();

        log.info("Extracted {} records", owlRecords.size());

        File outputDir = new File("owl2anything");
        outputDir.mkdirs();

        SuturoObjectsCsvPrinter.print(owlRecords, new File(outputDir, "suturo_objects.csv"));
        Id2NameJsonPrinter.print(owlRecords, new File(outputDir, "id2name.json"));
        YoloObjNamesPrinter.print(owlRecords, new File(outputDir, "obj.names"));
        CramObjectListPrinter.print(owlRecords, new File(outputDir, "cram_names.txt"));
    }
}
