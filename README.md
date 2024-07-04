# Owl2Anything Converter

[![Build and Test](https://github.com/SUTURO/Owl2Anything/actions/workflows/build-and-test.yml/badge.svg?branch=main)](https://github.com/SUTURO/Owl2Anything/actions/workflows/build-and-test.yml) [![Release (latest SemVer)](https://img.shields.io/github/v/release/SUTURO/Owl2Anything)](https://github.com/SUTURO/Owl2Anything/releases)

The [SUTURO](https://github.com/suturo) Owl2Anything Converter is a Java-based tool that can be used to convert OWL
ontology files into other formats like CSV and JSON. 

It can be used to extract information from an ontology and export
it into a tabular or annotation format, which can be easily imported into other tools or used for analysis. One of the
main features of the converter is the ability to export relevant information from the ontology into a CSV
file, which can be used to easily display the ontology in a searchable table format.

Examples of the exported formats can be found in the Downloads section of the Objects page of
the [SUTURO Knowledge website](https://suturo.github.io/suturo_knowledge/objects/).

## Requirements

- Java 17 or higher

## Usage

To use the Owl2Anything Converter, you need to run the Jar file with the following options:

```bash
java -jar owl2anything.jar -i [input_file] -r [root_class_iri] [-m [iri_mapping_file]] [-n [iri_namespaces_file]] [-l [nlp_mapping_file]]
```

The options are described below:

- `-i`, `--input`: Path to the input ontology file in OWL format (required).
- `-r`, `--root`: IRI of the root class. Every sublass of this IRI will be exported. (required).
- `-m`, `--mapping`: Path to the IRI mapping file (optional). If not specified, the default SUTURO IRI mapping will be
  used.
- `-n`, `--namespaces`: Path to the IRI namespaces file (optional). If not specified, the default SUTURO IRI namespaces
  will be used.
- `-l`, `--nlp`: Path to the nlp mappings file (optional). If not specified, no nlp mappings will be generated.

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

The NLP mappings file is a JSON file that maps IRI suffixes to a list of nlp names.
If one object type has no nlp mapping, it is assumed that the nlp name is the IRI suffix in lowercase with spaces between words, and a warning will be given.
Any mapping that is not the suffix of an IRI and does not start with an underscore `_` will cause warning and will be mapped to the root class
In case of two IRI classes that use the same name, a warning will be given and the lexically smaller class IRI will be used.
The file should have the following format:

```json
{
    "Trashbin":["trash bin", "trash can"],
    "Plasticknife":["plastic knife"],
    "Designedchair":["chair"],
    "Cupboard":["cupboard"],
    "Foambrick":["foam brick"],
    "Nesquickcerealbox":[]
}
```

## Contributing

### Recommend Code Editor

We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/) as code editor for this project with the following
plugins:

- Lombok

### Getting Started

1. Open the project in IntelliJ IDEA
2. Place the `suturo.owl` file in the projects root folder
3. Execute the Run-Configuration `Run Owl2Anything` or start the application via the main method in `Owl2Anything.java`
   with the required arguments.
4. The generated files will be placed in the `owl2anything/output` folder

### Create a Release

To publish a new release, you need to create a new tag with the version number and push it to the repository.
The release is then published automatically by the GitHub Actions [release.yml](.github/workflows/release.yml) workflow.

Example:

```bash
git tag -a 1.3.0 -m "v1.3.0" -m "- Fix bug in CSV export"
git push origin 1.3.0
```

- The tag name should be a valid [SemVer](https://semver.org/spec/v2.0.0.html) version number.
- The tag message head should start with a `v` prefix and then the tag name (e.g. `v1.3.0`).
- The tag message body should contain a short description of the changes since the last release.

To use the new release, change the `OWL2ANYTHING_VERSION` in
the [owl2anything.yml](https://github.com/SUTURO/suturo_knowledge/blob/master/.github/workflows/owl2anything.yml)
workflow of the [Knowledge Project](https://github.com/SUTURO/suturo_knowledge).

## License

The SUTURO Owl2Anything Converter is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file for
details.
