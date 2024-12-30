package agent.core;

import java.util.List;

public abstract class AbstractAgent {

    private boolean stopped = false;
    private final Environment environment;
    private final long pollingRate;

    public AbstractAgent(Environment environment, long pollingRate) {
        this.environment = environment;
        this.pollingRate = pollingRate;
    }

    /**
     * Sense phase. Fetch data from the environment
     */
    protected abstract void sense(Environment env);

    /**
     * Decide phase. Compute the decisions, after 'sense'
     * @return the list of actions
     */
    protected abstract List<AgentAction> decide();

    /**
     * Act phase. Apply all actions chosen.
     * @param actions the actions to apply
     */
    protected void act(List<AgentAction> actions) {
        actions.forEach(Runnable::run);
    }

    public void start() {
        new Thread(this::agentLoop).start();
        this.stopped = false;
    }
    public void stop() {
        this.stopped = true;
    }

    private void agentLoop() {
        while (!this.stopped) {

            this.sense(this.environment);
            this.act(this.decide());

            try {
                Thread.sleep(pollingRate);
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted");
            }
        }
    }
}
