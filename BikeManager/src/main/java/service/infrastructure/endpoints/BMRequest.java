package service.infrastructure.endpoints;

import io.micronaut.core.annotation.Introspected;
import service.domain.V2d;

/**
 * Body of a bike update request
 *
 * @param battery
 * @param position
 */
@Introspected
public record BMRequest(
        String id,
        int battery,
        V2d position
) {
}
