package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;

/**
 * Bodu of a user set request
 */
@Introspected
public record UserSetRequest(
        String username,
        int credits
) {}
