package service.infrastructure.db;

import jakarta.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class Config {
    public final static String databasePath = "db/.pwd";
    public static final Map<String, String> userTokens = new HashMap<>();
}
