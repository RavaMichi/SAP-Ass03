package service.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import service.application.AccountManager;
import service.application.AccountManagerListener;
import service.domain.User;

@Controller("/metrics")
public class AccountMetricServer implements AccountManagerListener {

    private final Counter userCount;
    private final Counter transactionCount;
    private PrometheusMeterRegistry prometheusRegistry;

    public AccountMetricServer(AccountManager accountManager, PrometheusMeterRegistry meterRegistry) {
        accountManager.addListener(this);
        this.prometheusRegistry = meterRegistry;

        // setup user counter
        this.userCount = Counter.builder("number_of_users")
                .description("The total number of users registered in the service")
                .register(meterRegistry);
        userCount.increment(accountManager.getAllUsers().size());

        // setup transaction counter
        this.transactionCount = Counter.builder("number_of_transactions")
                .description("The total number of transactions executed in the service")
                .register(meterRegistry);
    }

    @Override
    public void onUserAdded(User user) {
        userCount.increment();
    }

    @Override
    public void onUserCreditSet(User user, int oldAmount, int newAmount) {
        transactionCount.increment();
    }

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return prometheusRegistry.scrape();
    }

    @Get("/total-users")
    @Produces(MediaType.TEXT_PLAIN)
    public int totalUsers() {
        return (int)this.userCount.count();
    }

    @Get("/total-transactions")
    @Produces(MediaType.TEXT_PLAIN)
    public int totalTransactions() {
        return (int)this.transactionCount.count();
    }
}
