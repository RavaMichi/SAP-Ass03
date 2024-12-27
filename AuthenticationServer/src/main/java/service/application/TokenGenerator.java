package service.application;

import service.domain.User;

/** Outbound port
 * Service object, creates token for the logging users
 */
public interface TokenGenerator {
    String generate(User user);
}
