package com.malte3d.suturo.knowledge.owl2anything.input;

import java.io.File;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Parses and defines the cli arguments
 */
@Slf4j
@Value
public class StarterArgs {

    private static final String DEFAULT_CONFIG_DIR = "./owl2anything";

    private static final String ARG_ONTOLOGY_FILE       = "input";
    private static final String ARG_ROOT_IRI            = "root";
    private static final String ARG_IRI_MAPPING_FILE    = "mapping";
    private static final String ARG_IRI_NAMESPACES_FILE = "namespaces";

    File   ontologyFile;
    String iriRoot;
    File   iriMappingFile;
    File   iriNamespacesFile;

    public StarterArgs(String[] args) {

        Options options = new Options();

        options.addOption(
                Option.builder()
                        .option("i")
                        .longOpt(ARG_ONTOLOGY_FILE)
                        .hasArg()
                        .required()
                        .desc("Path to the input ontology file")
                        .build());

        options.addOption(
                Option.builder()
                        .option("r")
                        .longOpt(ARG_ROOT_IRI)
                        .hasArg()
                        .required()
                        .desc("IRI of the root class")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("m")
                        .longOpt(ARG_IRI_MAPPING_FILE)
                        .hasArg()
                        .desc("Path to the IRI mapping/resolution file")
                        .build()
        );

        options.addOption(
                Option.builder()
                        .option("n")
                        .longOpt(ARG_IRI_NAMESPACES_FILE)
                        .hasArg()
                        .desc("Path to the IRI namespaces file")
                        .build()
        );

        try {

            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            ontologyFile = new File(cmd.getOptionValue(ARG_ONTOLOGY_FILE));
            iriRoot = cmd.getOptionValue(ARG_ROOT_IRI);

            if (cmd.hasOption(ARG_IRI_MAPPING_FILE))
                iriMappingFile = new File(cmd.getOptionValue(ARG_IRI_MAPPING_FILE));
            else
                iriMappingFile = new File(DEFAULT_CONFIG_DIR, "iri_mapping_default.csv");

            if (cmd.hasOption(ARG_IRI_NAMESPACES_FILE))
                iriNamespacesFile = new File(cmd.getOptionValue(ARG_IRI_NAMESPACES_FILE));
            else
                iriNamespacesFile = new File(DEFAULT_CONFIG_DIR, "iri_namespaces_default.csv");

        } catch (ParseException e) {

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Owl2Anything Converter", options);

            throw new IllegalStateException("Error while parsing the command line arguments", e);

        }
    }

}
