package service.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import service.application.EventController;
import service.application.RideManager;
import service.domain.Ride;

import java.util.Date;

@Controller("/metrics")
public class RideMetricServer {

    private final Counter rideCount;
    private double avgRideDuration;
    private int finishedRides = 0;
    private PrometheusMeterRegistry prometheusRegistry;
    private EventController eventController;

    public RideMetricServer(RideManager rideManager, PrometheusMeterRegistry meterRegistry, EventController eventController) {
        this.prometheusRegistry = meterRegistry;
        this.eventController = eventController;
        // setup user counter
        this.rideCount = Counter.builder("number_of_users")
                .description("The total number of users registered in the service")
                .register(meterRegistry);
        rideCount.increment(rideManager.getAllRides().size());

        // setup average minute duration gauge
        Gauge.builder("average_ride_duration", this, RideMetricServer::getAvgRideDuration)
                .description("The average duration of all rides, in minutes")
                .register(meterRegistry);
        avgRideDuration = 0;

        eventController.whenRideStarted(this::onConnect);
        eventController.whenRideEnded(this::onDisconnect);
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return prometheusRegistry.scrape();
    }

    @Get("/total-rides")
    @Produces(MediaType.TEXT_PLAIN)
    public int totalUsers() {
        return (int)this.rideCount.count();
    }

    @Get("/avg-ride-duration")
    @Produces(MediaType.TEXT_PLAIN)
    public double getAvgRideDuration() {
        return this.avgRideDuration;
    }

    public void onConnect(Ride newRide) {
        this.rideCount.increment();
    }

    public void onDisconnect(Ride oldRide, Date endTime) {
        this.rideCount.increment(-1);
        var time = (endTime.getTime() - oldRide.startingTime().getTime()) / 60000;
        var total = avgRideDuration * finishedRides + time;
        finishedRides++;
        avgRideDuration = total / finishedRides;
    }
}
