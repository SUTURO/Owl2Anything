package com.malte3d.suturo.knowledge.owl2anything.output;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class to filter the given classes for perception classes.
 */
@UtilityClass
public class PerceptionClassesFilter {

    /**
     * Filters the given classes for perception classes.
     *
     * <p>
     * Perception classes are classes with a perception id.
     * </p>
     *
     * @param classes the classes to filter
     * @return the filtered classes
     */
    public static List<OwlRecord> filter(@NonNull List<OwlRecord> classes) {

        List<OwlRecord> perceptionClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        validateClasses(perceptionClasses);

        return perceptionClasses;
    }

    /**
     * Filters the given classes.
     *
     * @param classes the classes to filter
     * @return the filtered classes
     */
    public static List<OwlRecord> filterAll(@NonNull List<OwlRecord> classes) {

        List<OwlRecord> fixedClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() != null)
                .sorted(Comparator.comparing(OwlRecord::getPerceptionId))
                .toList();

        AtomicInteger counter = new AtomicInteger(fixedClasses.size());

        List<OwlRecord> additionalClasses = classes.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() == null)
                .sorted(Comparator.comparing(OwlRecord::getIriName))
                .map(record -> record.withPerceptionId(counter.getAndIncrement()))
                .toList();

        List<OwlRecord> output = Stream.concat(fixedClasses.stream(), additionalClasses.stream()).toList();

        validateClasses(output);

        return output;
    }

    private static void validateClasses(List<OwlRecord> records) {

        checkForIncompleteIds(records);
        checkForDuplicates(records);
    }

    private static void checkForIncompleteIds(List<OwlRecord> records) {

        List<OwlRecord> incompleteRecords = records.stream()
                .filter(csvRecord -> csvRecord.getPerceptionId() == null)
                .toList();

        if (!incompleteRecords.isEmpty())
            throw new IllegalStateException("Classes without Id found: " + incompleteRecords);

        boolean isSortedAndContinuous = records.stream()
                .mapToInt(OwlRecord::getPerceptionId)
                .reduce((int a, int b) -> b == (a + 1) ? b : Integer.MIN_VALUE)
                .orElse(Integer.MIN_VALUE) != Integer.MIN_VALUE;

        if (!isSortedAndContinuous)
            throw new IllegalStateException("Ids are not continuously increasing: " + records);
    }

    private static void checkForDuplicates(List<OwlRecord> records) {

        HashSet<Integer> seen = new HashSet<>(records.size());
        HashSet<OwlRecord> duplicate = new HashSet<>();

        for (OwlRecord csvRecord : records)
            if (!seen.add(csvRecord.getPerceptionId()))
                duplicate.add(csvRecord);

        if (!duplicate.isEmpty())
            throw new IllegalStateException("Duplicate perception Ids found: " + duplicate);
    }
}
