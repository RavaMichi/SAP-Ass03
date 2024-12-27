package service.infrastructure.auth;

public interface AuthChecker {
    boolean isAuthorized(String token);
}
