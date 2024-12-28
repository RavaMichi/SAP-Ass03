package service.application;

import java.util.Optional;

public interface AccountDatabase {
    void saveUser(String id, int credits);
    int getCredits(String id);
    boolean doesUserExist(String username);
    boolean doesUserHaveEnoughCredits(String username, int amount);
    void deductCreditsFromUser(String username, int amount);
}
