package service.infrastructure.endpoints;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

@Controller("/bikes/crash")
public class CrashAPI {

    @Get
    public void crash() {
        throw new RuntimeException("Programmed system crash");
    }
}
