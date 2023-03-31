package com.malte3d.suturo.knowledge.owl2anything.converter;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OwlRecord implements Comparable<OwlRecord> {

    String  iriName;
    String  iriNamespace;
    String  iriNamespaceShort;
    String  naturalName;
    String  description;
    Integer perceptionId;

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
