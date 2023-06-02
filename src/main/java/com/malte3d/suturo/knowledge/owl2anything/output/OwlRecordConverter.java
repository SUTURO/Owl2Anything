package com.malte3d.suturo.knowledge.owl2anything.output;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.experimental.UtilityClass;

/**
 * Converts an {@link OwlRecord} to the formats needed for the different interfaces
 */
@UtilityClass
public class OwlRecordConverter {

    /**
     * @param owlRecord the record to convert
     * @return the record in the format needed for the CRAM - KnowRob interface in Planning
     */
    public static String toKnowRobFormat(OwlRecord owlRecord) {
        return owlRecord.getIriNamespace() + owlRecord.getIriName();
    }

    /**
     * @param owlRecord the record to convert
     * @return the record in the format needed for the CRAM interface in Planning
     */
    public static String toCramFormat(OwlRecord owlRecord) {
        return owlRecord.getIriName().toLowerCase();
    }

    /**
     * @param owlRecord the record to convert
     * @return the record in the format needed for the CRAM - RoboKudo interface in Perception
     */
    public static String toRoboKudoFormat(OwlRecord owlRecord) {
        String lowerCaseName = owlRecord.getIriName().toLowerCase();
        return lowerCaseName.substring(0, 1).toUpperCase() + lowerCaseName.substring(1);
    }
}
