public class PCB {
    private int pid;
    private String name;
    private State state; // READY, RUNNING, BLOCKED
    private boolean active = false;

    /**
     * Creates a process control block for a given process
     * @param pid The process ID
     * @param name The process name
     * @param state The process state
     */
    public PCB(int pid, String name, State state) {
        this.pid = pid;
        this.name = name;
        this.state = state;
    }

    public static enum State {
        READY,
        RUNNING,
        BLOCKED;
    }

    public int getPid() {
        return this.pid;
    }

    public String getName() {
        return this.name;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return("PID " + pid + " [" + name + "] : " + this.state);
    }
}
