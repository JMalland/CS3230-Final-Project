import java.util.ArrayList;

public class ProcessManager {
    // Queue to hold all created processes
    private ArrayList<PCB> queue;
    // The PID given to the next process
    private int nextPid = 1;

    public ProcessManager() {
        queue = new ArrayList<>();
    }

    /**
     * Create a new process
     * 
     * @param name The process name
     */
    public void createProcess(String name) {
        queue.add(new PCB(nextPid++, name, PCB.State.READY)); // Updated to Ready Instead of Blocked - Hoyt Brem
        System.out.println("Created new process: [" + name + "]");
    }

    /**
     * Print each READY process as RUNNING
     * Simulate Round-Robin or other CPU scheduling algorithms
     * 
     * @throws InterruptedException
     */
    public void schedule() throws InterruptedException {
        for (PCB process : queue) {
            // The process is ready for execution
            if (process.getState() == PCB.State.READY) {
                // Execute this process
                process.setState(PCB.State.RUNNING);

                System.out.println("Running: " + process);

                // Simulate running time for 5 seconds
                Thread.sleep((int) (5000 * Math.random()));

                // Reset the process to READY
                process.setState(PCB.State.READY);
            }
        }
    }

    /**
     * Block a process from execution
     * 
     * @param pid The process ID
     */
    public void blockProcess(int pid) {
        if (queue.size() < pid) {
            System.out.println("Invalid process ID");
            return;
        }

        // Set process to blocked state
        queue.get(pid - 1).setState(PCB.State.BLOCKED);
    }

    /**
     * Allow a process to execute
     * 
     * @param pid The process ID
     */
    public void readyProcess(int pid) {
        if (queue.size() < pid) {
            System.out.println("Invalid process ID");
            return;
        }

        // Set process to ready state
        queue.get(pid - 1).setState(PCB.State.READY);
    }

    /**
     * Display all processes and their states
     */
    public void listProcesses() {
        // Go through each process, and print its info
        for (PCB process : queue) {
            System.out.println(process);
        }
    }
}
