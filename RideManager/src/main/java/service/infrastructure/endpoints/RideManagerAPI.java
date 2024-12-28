package service.infrastructure.endpoints;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.core.annotation.*;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.TaskExecutors;
import service.application.RideManager;
import service.domain.Ride;
import service.domain.RideManagerException;
import service.infrastructure.Config;
import service.infrastructure.auth.AuthChecker;

import java.util.*;

@Controller("/ride-service")
public class RideManagerAPI {

    private final RideManager rideManager;
    private final AuthChecker authChecker;

    public RideManagerAPI(RideManager rideManager, AuthChecker authChecker) {
        this.rideManager = rideManager;
        this.authChecker = authChecker;
    }

    @Get("/rides")
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<List<Ride>> getAllRides(@Header(HttpHeaders.AUTHORIZATION) String token) {
        if (authChecker.isAuthorized(token)) {
            return HttpResponse.ok(rideManager.getAllRides());
        } else {
            return HttpResponse.unauthorized();
        }
    }

    @Post("/connect")
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<RMResponse> connect(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            @Body RMRequest req
    ) {
        try {
            if (authChecker.isAuthorized(token)) {
                // memorize tokens
                Config.userTokens.put(req.userId(), token);
                Config.bikeTokens.put(req.bikeId(), token);
                // connect
                rideManager.connectUserToBike(req.userId(), req.bikeId());
                return HttpResponse.ok(new RMResponse("Successful connection", false));
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (RideManagerException e) {
            return HttpResponse.badRequest(new RMResponse(e.getMessage(), true));
        }
    }

    @Post("/disconnect")
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<RMResponse> disconnect(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            @Body RMRequest req
    ) {
        try {
            if (authChecker.isAuthorized(token)) {
                // disconnect
                rideManager.disconnectUserFromBike(req.userId(), req.bikeId());
                // forget tokens
                Config.userTokens.remove(req.userId());
                Config.bikeTokens.remove(req.bikeId());
                return HttpResponse.ok(new RMResponse("Successful disconnection", false));
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (RideManagerException e) {
            return HttpResponse.badRequest(new RMResponse(e.getMessage(), true));
        }
    }

    @Get("/{userId}/get")
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<Optional<Ride>> getRide(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            String userId
    ) {
        try {
            if (authChecker.isAuthorized(token)) {
                var ride = rideManager.getAllRides().stream()
                        .filter(r -> r.userId().equals(userId))
                        .findFirst();
                return HttpResponse.ok(ride);
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (RideManagerException e) {
            return HttpResponse.badRequest();
        }
    }

    @Introspected
    public record RMRequest(String userId, String bikeId) {}
    @Introspected
    public record RMResponse(String message, boolean error) {}
}
