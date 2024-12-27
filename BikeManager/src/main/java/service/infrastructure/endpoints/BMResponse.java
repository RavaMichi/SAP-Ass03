package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;

/**
 * Response to a bike update request
 *
 * @param message
 * @param error
 */
@Serdeable
public record BMResponse(
        String message,
        boolean error
) {
}
