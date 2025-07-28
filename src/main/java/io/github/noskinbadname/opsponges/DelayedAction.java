package io.github.noskinbadname.opsponges;

public class DelayedAction {
    private int delay;
    private final Runnable runnable;

    public DelayedAction(int delay, Runnable runnable) {
        this.delay = delay;
        this.runnable = runnable;
        OPSponges.delayedActions.add(this);
    }

    protected void onTick() {
        delay--;
        if (delay <= 0) {
            runnable.run();
            OPSponges.delayedActions.remove(this);
        }
    }
}
