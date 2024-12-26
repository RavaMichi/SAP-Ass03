package service.infrastructure.auth;

public interface AuthChecker {
    boolean hasUserPermissions(String token);
    boolean hasAdminPermissions(String token);
}
