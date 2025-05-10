import java.util.ArrayList;

public class ProcessManager {

    private ArrayList<PCB> queue;
    private int nextPid = 1;

    public ProcessManager() {
        queue = new ArrayList<>();
    }

    /**
     * Create a new process
     * @param name The process name
     */
    public void createProcess(String name) {
        queue.add(new PCB(nextPid ++, name, "READY"));
    }

    /**
     * Print each READY process as RUNNING
     * Simulate Round-Robin or other CPU scheduling algorithms
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
            System.out.println("PID " + process.getPid() + " [" + process.getName() + "] : " + process.getState());
        }
    }
}
