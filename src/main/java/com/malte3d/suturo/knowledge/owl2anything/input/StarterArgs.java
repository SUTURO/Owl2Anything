package com.malte3d.suturo.knowledge.owl2anything.input;

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

    static final String ARG_ONTOLOGY_FILE       = "input";
    static final String ARG_ROOT_IRI            = "root";
    static final String ARG_IRI_MAPPING_FILE    = "mapping";
    static final String ARG_IRI_NAMESPACES_FILE = "namespaces";

    String           ontologyFile;
    String           iriRoot;
    Optional<String> iriMappingFile;
    Optional<String> iriNamespacesFile;

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

            ontologyFile = cmd.getOptionValue(ARG_ONTOLOGY_FILE);
            iriRoot = cmd.getOptionValue(ARG_ROOT_IRI);
            iriMappingFile = Optional.ofNullable(cmd.getOptionValue(ARG_IRI_MAPPING_FILE));
            iriNamespacesFile = Optional.ofNullable(cmd.getOptionValue(ARG_IRI_NAMESPACES_FILE));

        } catch (ParseException e) {

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("Owl2Anything Converter", options);

            throw new IllegalStateException("Error while parsing the command line arguments", e);
        }
    }

}
