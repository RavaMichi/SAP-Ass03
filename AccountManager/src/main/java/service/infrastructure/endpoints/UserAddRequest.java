package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Bodu of a user add request
 */
@Serdeable
public record UserAddRequest(String username) {}
