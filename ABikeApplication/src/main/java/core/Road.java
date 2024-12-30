package core;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Value object. Represents a road in a road system.
 * @param name
 * @param oneWay
 * @param start
 * @param end
 */
@Serdeable
public record Road(String name, boolean oneWay, V2d start, V2d end) {
}
