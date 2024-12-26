package service.infrastructure.endpoints;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import service.application.BikeEndpoint;
import service.domain.BikeOperationException;

/**
 * Adapter for BikeEndpoint. Exposes a REST API for EBikes to use.
 */
@Controller("/bikes")
public class BikeEndpointAPI {

    private BikeEndpoint bikeEndpoint;

    public BikeEndpointAPI(BikeEndpoint bikeEndpoint) {
        this.bikeEndpoint = bikeEndpoint;
    }

    @Post("/{id}/update")
    public HttpResponse<BMResponse> updateBikeState(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            @Body BMRequest req,
            String id
    ) {
        try {
            if (isAuthorized(id, token)) {
                bikeEndpoint.updateBike(id, req.battery(), req.position());
                return HttpResponse.ok(new BMResponse("EBike " + id + " updated", false));
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (BikeOperationException e) {
            return HttpResponse.badRequest(new BMResponse(e.getMessage(), true));
        }
    }

    /**
     * Checks if the bike is authorized given th token
     */
    private boolean isAuthorized(String bikeId, String token) {
        // TODO improvement
        // simple authorization check
        return ("AUTHORIZED").equals(token);
    }

}
