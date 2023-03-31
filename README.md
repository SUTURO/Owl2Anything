# Owl2Anything Converter

The [SUTURO](https://github.com/suturo) Owl2Anything Converter is a Java-based tool that can be used to convert OWL
ontology
files into other formats like CSV and JSON. It can
be used to extract information from an ontology and export it into a tabular or annotation format, which can be easily
imported into
other tools or used for analysis.

## Requirements

- Java 17 or higher

## Usage

To use the Owl2Anything Converter, you need to run the Jar file with the following options:

```bash
java -jar owl2anything.jar -i [input_file] -r [root_class_iri] [-m [iri_mapping_file]] [-n [iri_namespaces_file]]
```

The options are described below:

- `-i`, `--input`: Path to the input ontology file in OWL format (required).
- `-r`, `--root`: IRI of the root class. Every sublass of this IRI will be exported. (required).
- `-m`, `--mapping`: Path to the IRI mapping file (optional).
- `-n`, `--namespaces`: Path to the IRI namespaces file (optional).

The IRI mapping file is a CSV file that maps (invalid) ontology IRIs to replacements. The file should have the
following format:

```text
original;replacement
"package://knowrob/owl/URDF.owl";"http://knowrob.org/owl/URDF.owl"
```

The IRI namespaces file is a CSV file that maps ontology IRIs to a short form namespace. The file should have the
following format:

```text
iri;namespace
http://www.ease-crc.org/ont/SUTURO.owl#;suturo
http://www.ease-crc.org/ont/SOMA.owl#;soma
```

## License

The SUTURO Owl2Anything Converter is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for details.