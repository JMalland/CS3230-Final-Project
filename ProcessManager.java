import java.util.ArrayList;

public class ProcessManager {
    // The time of program execution
    private static final long startTime = System.currentTimeMillis();
    
    // Queue to hold all created processes
    private ArrayList<PCB> queue;
    // The PID given to the next process
    private int nextPid = 1;

    public ProcessManager() {
        queue = new ArrayList<>();
    }

    /**
     * @return The age of the ProcessManager 
     */
    private static final long age() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Create a new process
     * @param name The process name
     */
    public void createProcess(String name) {
        queue.add(new PCB(nextPid ++, name, PCB.State.BLOCKED));
        System.out.println("Created new process: [" + name + "]");
    }

    /**
     * Print each READY process as RUNNING
     * Simulate Round-Robin or other CPU scheduling algorithms
     * @throws InterruptedException 
     */
    public void schedule() {

    }

    /**
     * Display all processes and their states
     */
    public void listProcesses() {
        // Clone the queue, and sort by PID
        ArrayList<PCB> copyQueue = new ArrayList<>(queue);
        copyQueue.sort((a, b) -> Integer.compare(a.getPid(), b.getPid()));

        // Go through each process, and print its info
        for (PCB process : copyQueue) {
            System.out.println(process);
        }
    }
}
