package com.malte3d.suturo.knowledge.owl2anything.converter;

import lombok.Builder;
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
    String iriName;

    /**
     * Ontology IRI
     */
    String iriNamespace;

    /**
     * SUTURO knowledge namespace shortform
     */
    String iriNamespaceShort;

    /**
     * A natural name for the OWL class, usually the class label
     */
    String naturalName;

    /**
     * Description for the OWL Class
     */
    String description;

    /**
     * SUTURO Perception Id
     */
    Integer perceptionId;

    /**
     * @return The combination of {@link #iriNamespaceShort} and {@link #iriName}
     * <p>
     * Example: suturo:'Apple'
     * </p>
     */
    public String getIriShortForm() {

        if (iriName == null || iriNamespaceShort == null)
            return null;

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
