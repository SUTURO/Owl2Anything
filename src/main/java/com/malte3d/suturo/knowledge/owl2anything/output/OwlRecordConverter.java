package com.malte3d.suturo.knowledge.owl2anything.output;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.experimental.UtilityClass;

/**
 * Converts an {@link OwlRecord} to the formats needed for the different interfaces
 */
@UtilityClass
public class OwlRecordConverter {

    public static String toKnowRobFormat(OwlRecord owlRecord) {
        return owlRecord.getIriShortForm();
    }

    public static String toCramFormat(OwlRecord owlRecord) {
        return owlRecord.getIriName().toLowerCase();
    }

    public static String toRoboKudoFormat(OwlRecord owlRecord) {
        String lowerCaseName = owlRecord.getIriName().toLowerCase();
        return lowerCaseName.substring(0, 1).toUpperCase() + lowerCaseName.substring(1);
    }
}
