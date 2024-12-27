package service.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Entity object. Represents a recharge station. Used by A-Bikes
 */
@Serdeable
public record Station(String id, V2d position) implements java.io.Serializable {
}
