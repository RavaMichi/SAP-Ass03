package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;

/**
 * Bodu of a user add request
 */
@Introspected
public record UserAddRequest(String username) {}
