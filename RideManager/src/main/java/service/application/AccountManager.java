package service.application;

/** Inbound port
 */
public interface AccountManager {
    boolean doesUserExist(String username);
    boolean doesUserHaveEnoughCredits(String username, int amount);
    void deductCreditsFromUser(String username, int amount);
}
