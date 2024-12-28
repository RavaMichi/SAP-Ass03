package service.domain;

import io.micronaut.core.annotation.Introspected;

import java.util.Date;

/**
 * Entity object. Represents a rental session of a user.
 * There cannot be two rides with either the same user or bike.
 */
@Introspected
public record Ride(String userId, String bikeId, Date startingTime) implements java.io.Serializable {
}
