package com.malte3d.suturo.knowledge.owl2anything.converter;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Combines extracted information from an OWL Class.
 */
@Value
@Builder
public class OwlRecord implements Comparable<OwlRecord> {

    /**
     * OWL class name
     */
    @NonNull
    String iriName;

    /**
     * Ontology IRI
     */
    @NonNull
    String iriNamespace;

    /**
     * SUTURO knowledge namespace shortform
     */
    @NonNull
    String iriNamespaceShort;

    /**
     * A natural name for the OWL class, usually the class label
     */
    @NonNull
    String naturalName;

    /**
     * Description for the OWL Class
     */
    @NonNull
    String description;

    /**
     * SUTURO Perception Id
     */
    Integer perceptionId;

    /**
     * Default size for the OWL Class
     */
    Size defaultSize;

    /**
     * @return The combination of {@link #iriNamespaceShort} and {@link #iriName}
     * <p>
     * Example: suturo:'Apple'
     * </p>
     */
    @NonNull
    public String getIriShortForm() {
        return iriNamespaceShort + ":'" + iriName + "'";
    }

    /**
     * Can be used to sort a list of {@link OwlRecord}es by their {@link #iriName}.
     */
    @Override
    public int compareTo(OwlRecord other) {
        return iriName.compareTo(other.iriName);
    }
}
