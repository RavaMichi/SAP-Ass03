package service.infrastructure.endpoints;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import service.application.BikeManager;
import service.domain.BikeOperationException;
import service.domain.EBike;
import service.infrastructure.auth.AuthChecker;

import java.util.List;
import java.util.Optional;

@Controller("/bikes")
public class BikeManagerAPI {
    private BikeManager bikeManager;
    private AuthChecker authChecker;

    public BikeManagerAPI(BikeManager bikeManager, AuthChecker authChecker) {
        this.bikeManager = bikeManager;
        this.authChecker = authChecker;
    }

    @Get
    public HttpResponse<List<EBike>> getAllBikes(@Header(HttpHeaders.AUTHORIZATION) String token) {
        if (authChecker.hasUserPermissions(token)) {
            return HttpResponse.ok(bikeManager.getAllBikes());
        } else {
            return HttpResponse.unauthorized();
        }
    }

    @Get("/{id}")
    public HttpResponse<Optional<EBike>> getBike(@Header(HttpHeaders.AUTHORIZATION) String token, String id) {
        if (authChecker.hasUserPermissions(token)) {
            return HttpResponse.ok(bikeManager.getBike(id));
        } else {
            return HttpResponse.unauthorized();
        }
    }

    @Post("/add")
    public HttpResponse<BMResponse> addBike(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            @Body BMRequest req
    ) {
        try {
            if (authChecker.hasAdminPermissions(token)) {
                bikeManager.addBike(req.id(), req.battery(), req.position());
                return HttpResponse.created(new BMResponse("EBike " + req.id() + " added", false));
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (BikeOperationException e) {
            return HttpResponse.badRequest(new BMResponse(e.getMessage(), true));
        }
    }
}
