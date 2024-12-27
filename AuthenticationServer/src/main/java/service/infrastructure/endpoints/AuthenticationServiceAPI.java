package service.infrastructure.endpoints;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.TaskExecutors;
import service.application.AuthenticationService;
import service.domain.AuthenticationException;
import service.infrastructure.db.Config;

@Controller("/auth")
public class AuthenticationServiceAPI {

    private final AuthenticationService authenticationService;
    public AuthenticationServiceAPI(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Post("/login")
    @Produces(MediaType.TEXT_PLAIN)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<String> login(@Body LoginRequest req) {
        try {
            // create temp token
            var token = authenticationService.generateToken(req.username(), req.password());
            Config.userTokens.put(req.username(), token);
            // check authentication
            token = authenticationService.authenticate(req.username(), req.password());
            // forget temp token
            Config.userTokens.remove(req.username());
            return HttpResponse.ok(token);
        } catch (AuthenticationException e) {
            return HttpResponse.badRequest();
        }
    }

    @Post("/register")
    @Produces(MediaType.TEXT_PLAIN)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<String> register(@Body LoginRequest req) {
        try {
            // create temp token
            var token = authenticationService.generateToken(req.username(), req.password());
            Config.userTokens.put(req.username(), token);
            // registration
            token = authenticationService.register(req.username(), req.password());
            // forget temp token
            Config.userTokens.remove(req.username());
            return HttpResponse.ok(token);
        } catch (AuthenticationException e) {
            return HttpResponse.badRequest();
        }
    }

    public record LoginRequest(String username, String password) {}
}
