package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Bodu of a user set request
 */
@Serdeable
public record UserSetRequest(
        String username,
        int credits
) {}
