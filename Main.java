import java.util.Scanner;


/* Possible Bonus Points (10 Pts Each):
 *   Deadlock Handling
 *     - Implement deadlock detection or avoidance
 *   Virtual Paging & Memory Visualization
 *   GUI Interface
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {
        // Create commandline input scanner
        Scanner input = new Scanner(System.in);

        ProcessManager processMgr = new ProcessManager();
        MemoryManager memoryMgr = new MemoryManager(100);

        // Continue running as long as the input has a new line
        while (input.hasNextLine()) {
            // The inputted command
            String line = input.nextLine();

            // Check the first word against the commands list
            // Ignores invalid casing
            switch(line.split(" ", 2)[0].toLowerCase()) {
                // Create a new process
                case("create"):
                    processMgr.createProcess(line.split(" ", 2)[1]);
                    break;
                // Display processes information
                case("ps"):
                    processMgr.listProcesses();
                    break;
                // Display process schedule information
                case("schedule"):
                    processMgr.schedule();
                    break;
                // Allocate memory to a process
                case("alloc"):
                    // Get the PID
                    int pid = Integer.parseInt(line.split(" ", 4)[1]);
                    // Get the allocation size
                    int size = Integer.parseInt(line.split(" ", 4)[2]);

                    // Attempt to allocate memory
                    memoryMgr.allocate(pid, size);
                    break;
                // Display memory information
                case("mem"):
                    memoryMgr.printMemory();
                    break;
                // Exit the program
                case("exit"):
                    return;
            }

        }

        // Close the input stream
        input.close();
    }
}