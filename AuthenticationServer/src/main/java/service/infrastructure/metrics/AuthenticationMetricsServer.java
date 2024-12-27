package service.infrastructure.metrics;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;

@Controller("/metrics")
public class AuthenticationMetricsServer {

    private PrometheusMeterRegistry prometheusRegistry;

    public AuthenticationMetricsServer(PrometheusMeterRegistry meterRegistry) {
        this.prometheusRegistry = meterRegistry;
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return prometheusRegistry.scrape();
    }
}
