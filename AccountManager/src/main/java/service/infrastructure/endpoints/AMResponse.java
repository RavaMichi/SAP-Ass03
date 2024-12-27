package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;

/**
 * Response to a request
 */
@Introspected
public record AMResponse(
        String message,
        boolean error
) {
}
