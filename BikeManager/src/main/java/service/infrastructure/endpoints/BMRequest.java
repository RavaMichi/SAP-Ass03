package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import service.domain.V2d;

/**
 * Body of a bike update request
 *
 * @param battery
 * @param position
 */
@Serdeable
public record BMRequest(
        String id,
        int battery,
        V2d position
) {
}
