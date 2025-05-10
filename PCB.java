public class PCB {
    private int pid;
    private String name;
    private String state; // READY, RUNNING, BLOCKED
    private boolean active = false;

    /**
     * Creates a process control block for a given process
     * @param pid The process ID
     * @param name The process name
     * @param state The process state
     */
    public PCB(int pid, String name, String state) {
        this.pid = pid;
        this.name = name;
        this.state = state;
    }

    public int getPid() {
        return this.pid;
    }

    public String getName() {
        return this.name;
    }

    public String getState() {
        return this.state;
    }

    public boolean isActive() {
        return this.active;
    }
}
