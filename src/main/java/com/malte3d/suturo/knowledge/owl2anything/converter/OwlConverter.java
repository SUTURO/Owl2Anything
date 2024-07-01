package com.malte3d.suturo.knowledge.owl2anything.converter;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import com.malte3d.suturo.knowledge.owl2anything.input.IriMappingParser;
import com.malte3d.suturo.knowledge.owl2anything.input.IriNamespacesParser;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;

/**
 * The main class for converting an OWL ontology to a custom format.
 */
@Slf4j
public class OwlConverter {

    private static final Pattern LINE_BREAKS = Pattern.compile("[\\r\\n]+");

    private final String iriRoot;
    private final Map<String, String> iriMapping;
    private final Map<String, String> iriNamespaces;

    private final OWLOntology ontology;
    private final Set<OWLOntology> allOntologies;

    @Builder
    public OwlConverter(@NonNull File ontologyFile, @NonNull String iriRoot, @NonNull File iriMappingFile, @NonNull File iriNamespacesFile) {

        this.iriRoot = iriRoot;
        this.iriMapping = IriMappingParser.getIriMapping(iriMappingFile);
        this.iriNamespaces = IriNamespacesParser.getIriNamespaces(iriNamespacesFile);

        OWLOntologyManager manager = createOntologyManager();
        OWLOntologyLoaderConfiguration config = createOntologyLoaderConfiguration();

        try {

            this.ontology = manager.loadOntologyFromOntologyDocument(new FileDocumentSource(ontologyFile), config);
            this.allOntologies = manager.getOntologies();

        } catch (OWLOntologyCreationException e) {
            log.error("Error while loading the ontology {}", ontologyFile.getPath(), e);
            throw new IllegalStateException(e);
        }

    }

    /**
     * Extracts and builds a list of {@link OwlRecord} from the given {@link #ontology}
     *
     * @return List of all {@link OwlRecord}, that are a subclass of {@link #iriRoot}
     */
    public List<OwlRecord> extractRecords() {

        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);

        /* Get the subclasses to be exported */
        OWLClass rootClass = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLClass(IRI.create(iriRoot));
        Set<OWLClass> subclasses = reasoner.getSubClasses(rootClass, false).getFlattened();

        List<OwlRecord> owlRecords = new ArrayList<>(subclasses.size());

        /* Extract the relevant values from the ontology */
        for (OWLClass owlClass : subclasses) {

            String iriName = owlClass.getIRI().getShortForm();
            String iriNamespace = owlClass.getIRI().getNamespace();
            String iriNamespaceShort = iriNamespaces.get(iriNamespace);
            String naturalName = extractNaturalName(owlClass);
            String description = extractDescription(owlClass);
            List<Integer> perceptionIds = extractPerceptionId(owlClass);
            List<String> predefinedNames = extractPredefinedNames(owlClass);

            // Skip "Nothing"
            if ("http://www.w3.org/2002/07/owl#Nothing".equals(owlClass.getIRI().getIRIString()))
                continue;

            if(perceptionIds.isEmpty()) {

                OwlRecord owlRecord = OwlRecord.builder()
                        .iriName(iriName)
                        .iriNamespace(iriNamespace)
                        .iriNamespaceShort(iriNamespaceShort)
                        .naturalName(naturalName)
                        .description(description)
                        .perceptionId(null)
                        .predefinedNames(predefinedNames)
                        .build();

                owlRecords.add(owlRecord);

            } else {

                for(int perceptionId : perceptionIds) {

                    OwlRecord owlRecord = OwlRecord.builder()
                            .iriName(iriName)
                            .iriNamespace(iriNamespace)
                            .iriNamespaceShort(iriNamespaceShort)
                            .naturalName(naturalName)
                            .description(description)
                            .perceptionId(perceptionId)
                            .predefinedNames(predefinedNames)
                            .build();

                    owlRecords.add(owlRecord);
                }
            }
        }

        /* Sort the records lexicographically */
        owlRecords.sort(OwlRecord::compareTo);

        reasoner.dispose();

        return owlRecords;
    }

    private List<String> extractPredefinedNames(OWLClass owlClass) {
        OWLDataProperty hasPredefinedName = new OWLDataPropertyImpl(IRI.create("http://www.ease-crc.org/ont/SUTURO.owl#hasPredefinedName"));
        return EntitySearcher.getSuperClasses(owlClass, allOntologies.stream())
            .filter(cls -> cls.getClassExpressionType() == ClassExpressionType.DATA_HAS_VALUE)
            .map(cls -> (OWLDataHasValue) cls)
            .filter(cls -> cls.getProperty().equals(hasPredefinedName))
            .map(HasFiller::getFiller)
            .map(OWLLiteral::getLiteral)
            .toList();
    }

    /**
     * Extracts the perception id from the annotation "suturo:perceptionId"
     *
     * @return The pereception id or null
     */
    private List<Integer> extractPerceptionId(OWLClass owlClass) {

        OWLAnnotationPropertyImpl idAnnotationProperty = new OWLAnnotationPropertyImpl(IRI.create("http://www.ease-crc.org/ont/SUTURO.owl#perceptionId"));

        return EntitySearcher.getAnnotations(owlClass, allOntologies.stream(), idAnnotationProperty)
                .map(OWLAnnotation::getValue)
                .map(value -> value.asLiteral().map(owlLiteral -> owlLiteral.components().toArray()[1].toString()))
                .map(perceptionIdString -> perceptionIdString.map(Integer::valueOf).orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Extracts the natural name from the annotation "rdfs:label"
     *
     * @return The label if present, else the class name
     */
    private String extractNaturalName(OWLClass owlClass) {

        OWLAnnotationPropertyImpl labelAnnotationProperty = new OWLAnnotationPropertyImpl(IRI.create("http://www.w3.org/2000/01/rdf-schema#label"));

        String naturalName = EntitySearcher.getAnnotations(owlClass, allOntologies.stream(), labelAnnotationProperty)
                .findFirst()
                .map(OWLAnnotation::getValue)
                .flatMap(value -> value.asLiteral().map(owlLiteral -> owlLiteral.components().toArray()[1].toString()))
                .orElse(owlClass.getIRI().getShortForm());

        return removeLineBreaks(naturalName);
    }

    /**
     * Extracts the longest description from the annotation "rdfs:comment"
     *
     * @return The description if present, else the class name
     */
    private String extractDescription(OWLClass owlClass) {

        OWLAnnotationPropertyImpl commentAnnotationProperty = new OWLAnnotationPropertyImpl(IRI.create("http://www.w3.org/2000/01/rdf-schema#comment"));

        String description = EntitySearcher.getAnnotations(owlClass, allOntologies.stream(), commentAnnotationProperty)
                .collect(Collectors.toSet())
                .stream()
                .max((o1, o2) -> literalLengthComparator().compare(o1.getValue(), o2.getValue()))
                .map(OWLAnnotation::getValue)
                .flatMap(value -> value.asLiteral().map(owlLiteral -> owlLiteral.components().toArray()[1].toString()))
                .orElse(owlClass.getIRI().getShortForm());

        return removeLineBreaks(description);
    }

    /**
     * Comparator to compare {@link OWLLiteral} by their length
     */
    private static Comparator<OWLAnnotationValue> literalLengthComparator() {

        return (a, b) -> {

            if (a instanceof OWLLiteral aLiteral && b instanceof OWLLiteral bLiteral)
                return Integer.compare(aLiteral.getLiteral().length(), bLiteral.getLiteral().length());

            return 0;
        };
    }

    /**
     * Replaces all linebreaks in the given text by a whitespace
     *
     * @return String without line breaks
     */
    private static String removeLineBreaks(String text) {
        return LINE_BREAKS.matcher(text).replaceAll(" ");
    }

    private OWLOntologyManager createOntologyManager() {

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        manager.getIRIMappers().add(createOntologyIriMapper());

        return manager;
    }

    private OWLOntologyIRIMapper[] createOntologyIriMapper() {

        Set<OWLOntologyIRIMapper> iriMappers = new HashSet<>(iriMapping.size());

        iriMapping.forEach((original, replacement) -> {
            OWLOntologyIRIMapper mapper = new SimpleIRIMapper(IRI.create(original), IRI.create(replacement));
            iriMappers.add(mapper);
        });

        return iriMappers.toArray(new OWLOntologyIRIMapper[0]);
    }

    private static OWLOntologyLoaderConfiguration createOntologyLoaderConfiguration() {
        return new OWLOntologyLoaderConfiguration().setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
    }
}
