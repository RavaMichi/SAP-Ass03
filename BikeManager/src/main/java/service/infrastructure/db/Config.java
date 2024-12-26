package service.infrastructure.db;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import service.infrastructure.auth.AuthChecker;

@Controller("/config")
public class Config {
    public static String bikeDatabasePath = "db/.bikes";
    public static String stationDatabasePath = "db/.stations";

    private final AuthChecker authChecker;
    private final FileBikeDatabase fileBikeDatabase;

    public Config(AuthChecker authChecker, FileBikeDatabase fileBikeDatabase) {
        this.authChecker = authChecker;
        this.fileBikeDatabase = fileBikeDatabase;
    }

    @Post("/set-db-path")
    public HttpResponse<String> setDbPath(@Header(HttpHeaders.AUTHORIZATION) String token, @Body String dbPath) {
        if (authChecker.hasAdminPermissions(token)) {
            bikeDatabasePath = dbPath;
            fileBikeDatabase.init(bikeDatabasePath);
            return HttpResponse.ok(bikeDatabasePath);
        } else {
            return HttpResponse.unauthorized();
        }
    }
}
