package service.infrastructure.metrics;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micrometer.core.instrument.*;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import service.application.BikeManager;
import service.application.EventController;
import service.domain.EBike;

@Controller("/metrics")
public class BikeMetricsServer {

    private final Counter totalBikes;
    private double avgBatteryLevel;
    private PrometheusMeterRegistry prometheusRegistry;
    private EventController eventController;

    public BikeMetricsServer(BikeManager bikeManager, PrometheusMeterRegistry meterRegistry, EventController eventController) {
        this.prometheusRegistry = meterRegistry;
        this.eventController = eventController;

        // track the number of bikes
        totalBikes = Counter.builder("number_of_bikes")
                .description("The total number of bikes registered in the service")
                .register(meterRegistry);

        // track the average battery level
        Gauge.builder("average_battery", this, BikeMetricsServer::getAvgBatteryLevel)
                .description("The average level of battery of all bikes")
                .register(meterRegistry);

        // init all counters
        totalBikes.increment(bikeManager.getAllBikes().size());
        avgBatteryLevel = bikeManager.getAllBikes().stream()
                .mapToInt(EBike::getBatteryLevel)
                .average()
                .orElse(0.0);

        System.out.println("Prometheus ready");

        eventController.whenBikeAdded(this::onBikeAdd);
        eventController.whenBikeUpdated(this::onBikeUpdate);
    }

    private double getAvgBatteryLevel() {
        return avgBatteryLevel;
    }

    public void onBikeAdd(EBike newBike) {
        totalBikes.increment();
        avgBatteryLevel = recalculateAverage(newBike.getBatteryLevel(), true);
    }

    public void onBikeUpdate(EBike previousBike, EBike updatedBike) {
        avgBatteryLevel = recalculateAverage(updatedBike.getBatteryLevel() - previousBike.getBatteryLevel(), false);
    }

    private double recalculateAverage(double delta, boolean isAddition) {
        double totalBikesCount = totalBikes.count();
        double currentTotalBattery = avgBatteryLevel * totalBikesCount;

        return (currentTotalBattery + delta) / totalBikesCount;
    }
    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return prometheusRegistry.scrape();
    }

    @Get("/total-bikes")
    public int totalBikesCount() {
        return (int)totalBikes.count();
    }

    @Get("/avg-battery")
    public double averageBatteryLevel() {
        return avgBatteryLevel;
    }
}
