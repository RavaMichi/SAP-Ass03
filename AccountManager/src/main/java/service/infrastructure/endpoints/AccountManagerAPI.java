package service.infrastructure.endpoints;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import service.application.AccountManager;
import service.domain.*;
import service.infrastructure.auth.AuthChecker;

import java.util.List;
import java.util.Optional;

@Controller("/users")
public class AccountManagerAPI {
    private AccountManager accountManager;
    private AuthChecker authChecker;

    public AccountManagerAPI(AccountManager accountManager, AuthChecker authChecker) {
        this.accountManager = accountManager;
        this.authChecker = authChecker;
    }

    @Get
    public HttpResponse<List<User>> getAllUsers(@Header(HttpHeaders.AUTHORIZATION) String token) {
        if (authChecker.isAuthorized(token)) {
            return HttpResponse.ok(accountManager.getAllUsers());
        } else {
            return HttpResponse.unauthorized();
        }
    }

    @Get("/{username}")
    public HttpResponse<Optional<User>> getUser(@Header(HttpHeaders.AUTHORIZATION) String token, String username) {
        if (authChecker.isAuthorized(token)) {
            return HttpResponse.ok(accountManager.getUser(username));
        } else {
            return HttpResponse.unauthorized();
        }
    }

//    @Post("/add")
//    public HttpResponse<AMResponse> addUser(
//            @Header(HttpHeaders.AUTHORIZATION) String token,
//            @Body UserAddRequest req
//    ) {
//        try {
//            if (authChecker.isAuthorized(token)) {
//                accountManager.addUser(req.username());
//                return HttpResponse.created(new AMResponse("User " + req.username() + " added", false));
//            } else {
//                return HttpResponse.unauthorized();
//            }
//        } catch (AccountOperationException e) {
//            return HttpResponse.badRequest(new AMResponse(e.getMessage(), true));
//        }
//    }

    @Post("/{username}/add-credit")
    public HttpResponse<AMResponse> addCreditUser(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            @Body UserSetRequest req
    ) {
        try {
            if (authChecker.isAuthorized(token)) {
                accountManager.addCreditToUser(req.username(), req.credits());
                return HttpResponse.created(new AMResponse("User " + req.username() + " updated", false));
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (AccountOperationException e) {
            return HttpResponse.badRequest(new AMResponse(e.getMessage(), true));
        }
    }
    @Post("/{username}/remove-credit")
    public HttpResponse<AMResponse> removeCreditUser(
            @Header(HttpHeaders.AUTHORIZATION) String token,
            @Body UserSetRequest req
    ) {
        try {
            if (authChecker.isAuthorized(token)) {
                accountManager.removeCreditFromUser(req.username(), req.credits());
                return HttpResponse.created(new AMResponse("User " + req.username() + " updated", false));
            } else {
                return HttpResponse.unauthorized();
            }
        } catch (AccountOperationException e) {
            return HttpResponse.badRequest(new AMResponse(e.getMessage(), true));
        }
    }
}
