package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Response to a request
 */
@Serdeable
public record AMResponse(
        String message,
        boolean error
) {
}
