package com.malte3d.suturo.knowledge.owl2anything.output;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.experimental.UtilityClass;

import java.util.Locale;
import java.util.regex.Pattern;

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

    private static final Pattern CAPITAL_LETTER = Pattern.compile("([A-Z])");
    private static final Pattern CAPITAL_LETTER_GROUP = Pattern.compile("([A-Z])_([A-Z])");

    /**
     * @param owlRecord the record to convert
     * @return the record in the format needed for the PyCRAM variable names
     */
    public static String toPycramFormat(OwlRecord owlRecord) {
        String name = owlRecord.getIriName();
        // First convert "CerealBox" and "ToyotaHSR" to "_Cereal_Box" and "_Toyota_H_S_R"
        // aka prefix all capital letters with an _
        name = CAPITAL_LETTER.matcher(name).replaceAll("_$1");
        // Then convert "_Cereal_Box" and "_Toyota_H_S_R" to "_Cereal_Box" and "_Toyota_HS_R"
        // aka remove underscores between capital letters
        name = CAPITAL_LETTER_GROUP.matcher(name).replaceAll("$1$2");
        // Then convert "_Cereal_Box" and "_Toyota_HS_R" to "_Cereal_Box" and "_Toyota_HSR"
        // aka remove the underscores not removed in the step before,
        // since the left capital letter was already part of a replacement in that step
        name = CAPITAL_LETTER_GROUP.matcher(name).replaceAll("$1$2");

		// Lastly convert "_Cereal_Box" and "_Toyota_HSR" to "cereal_box" and "toyota_hsr"
        // aka remove the underscore at the beginning (since all names start with a capital letter)
        // and convert it to lower case

		// since the current suturo_perception group made some object names without a capital letter at the start, only remove in that case.
		if (name.startsWith("_")) {
			name = name.substring(1);
		}
        return name.toLowerCase();
    }

    /**
     * @param owlRecord the record to convert
     * @return the record in the format needed for the CRAM - RoboKudo interface in Perception
     */
    public static String toRoboKudoFormat(OwlRecord owlRecord) {
		String iriName = owlRecord.getIriName();
        String restLowerCaseName = iriName.substring(1).toLowerCase(Locale.ROOT);
        return iriName.charAt(0) + restLowerCaseName;
    }

    public static String toFallbackNlpFormat(OwlRecord record) {
        return toPycramFormat(record).replace('_', ' ');
    }
}
