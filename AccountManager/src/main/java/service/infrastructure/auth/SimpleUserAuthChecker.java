package service.infrastructure.auth;

import jakarta.inject.Singleton;

@Singleton
public class SimpleUserAuthChecker implements AuthChecker {
    @Override
    public boolean isAuthorized(String token) {
        return token.equals("AUTHORIZED");
    }
}
