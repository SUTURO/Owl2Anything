package com.malte3d.suturo.knowledge.owl2anything.output;

import com.malte3d.suturo.knowledge.owl2anything.converter.OwlRecord;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OwlRecordConverterTest {

    @ParameterizedTest
    @CsvSource({
            "CerealBox, cereal_box",
            "ToyotaHSR, toyota_hsr",
            "JellOChocolatePuddingBox, jell_ochocolate_pudding_box",
            "Drawer, drawer"
    })
    void toPycramFormat(String iriName, String pycramName) {
        OwlRecord record = OwlRecord.builder()
                // The actual test data
                .iriName(iriName)
                // and the other values with empty strings since lombok does not allow them to be null
                .iriNamespace("")
                .iriNamespaceShort("")
                .naturalName("")
                .description("")
                .predefinedNames(List.of())
                .build();
        assertEquals(pycramName, OwlRecordConverter.toPycramFormat(record));
    }
}
