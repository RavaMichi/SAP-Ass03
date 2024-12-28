package service.infrastructure;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Config {
    public static final String databasePath = "db/.rides";
    public static final Map<String, String> userTokens = new HashMap<>();
    public static final Map<String, String> bikeTokens = new HashMap<>();
}
