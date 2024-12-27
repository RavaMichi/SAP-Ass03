package service.application;

import service.domain.AuthenticationException;

/** Inbound port
 * Service object, representing the authenticator
 */
public interface AuthenticationService {
    String generateToken(String username, String password) throws AuthenticationException;
    String authenticate(String username, String password) throws AuthenticationException;
    String register(String username, String password) throws AuthenticationException;
}
