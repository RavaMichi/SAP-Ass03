package service.infrastructure.auth;

import jakarta.inject.Singleton;

@Singleton
public class SimpleUserAuthChecker implements AuthChecker {
    @Override
    public boolean hasUserPermissions(String token) {
        return token.equals("AUTHORIZED");
    }

    @Override
    public boolean hasAdminPermissions(String token) {
        return token.equals("ADMIN");
    }
}
