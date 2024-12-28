package service.domain;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Date;

/**
 * Entity object. Represents a rental session of a user.
 * There cannot be two rides with either the same user or bike.
 */
@Serdeable
public record Ride(String userId, String bikeId, Date startingTime) implements java.io.Serializable {
}
