package com.malte3d.suturo.knowledge.owl2anything.converter;

import lombok.Value;

/**
 * Combines the default sizes for an OWL Class.
 *
 * <p>
 * The base unit is meter.
 * </p>
 */
@Value
public class Size {

    double width;
    double height;
    double depth;

}
