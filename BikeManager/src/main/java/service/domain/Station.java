package service.domain;

import io.micronaut.core.annotation.Introspected;
/**
 * Entity object. Represents a recharge station. Used by A-Bikes
 */
@Introspected
public record Station(String id, V2d position) implements java.io.Serializable {
}
