import java.util.ArrayList;
import java.util.HashMap;

/**
 * What I'm currently thinking would be best to implement MemoryManager, is to store each RAM
 * allocation (Range) as a list within a HashMap, per each PID, with 0 being free. If the client 
 * allocates more RAM to a process than currently free, release the memory in-use, and wait.
 */
public class MemoryManager {
    // Used a fixed-size array to simulate memory blocks
    private int[] memory;
    private HashMap<Integer, ArrayList<Range>> table;
    private Semaphore2 semaphore = new Semaphore2();

    public MemoryManager(int maxBytes) throws InterruptedException {
        // Acquire the semaphore lock
        semaphore.waitSem();

        // Initialize the memory
        memory = new int[maxBytes];

        // Initialize the HashMap<PID, List<Range>>
        table = new HashMap<>();

        // Add the free memory (PID == 0)
        table.putIfAbsent(0, new ArrayList<Range>());
        // Record the initial free memory
        table.get(0).add(new Range(0, maxBytes));

        // Release the semaphore lock
        semaphore.signal();
    }

    /**
     * Use First-Fit, Best-Fit, or Worst-Fit to find
     * a free region and store the PID in that region
     * @param pid The process ID
     * @param size The processes memory size
     * @throws InterruptedException 
     */
    public void allocate(int pid, int size) throws InterruptedException {
        // The allocation request is too large
        if (size > memory.length) {
            System.out.println("Cannot fullfill allocation request: Memory Index Out of Bounds");
            return;
        }

        // Acquire the semaphore lock
        semaphore.waitSem();

        // Get all blocks of free memory
        ArrayList<Range> freeMemory = table.get(0);

        // Store the best fitting range, for this allocation
        // Initially none
        Range bestFit = null;

        // Go through each block of free memory
        for (Range block : freeMemory) {
            // The block is large enough, and is the smallest block yet found
            if (bestFit == null || block.size() >= size && block.size() < bestFit.size()) {
                // Save the smallest available block
                bestFit = block;
            }
        }

        // The allocation request is too large, so release all held memory and wait for a large enough block
        if (bestFit == null) {
            // Release the semaphore lock
            semaphore.signal();

            // Release all memory held by process
            this.free(pid);

            // Wait for a large enough allocation block
            allocate(pid, size);

            // Quit the function
            return;
        }

        // Remove the best-fit block from the table
        // because we will break it into two blocks
        table.get(0).remove(bestFit);

        // Create the smallest block to hold the allocation.
        Range allocated = new Range(bestFit.start(), bestFit.start() + size);

        // Create the allocation list for the process
        table.putIfAbsent(pid, new ArrayList<Range>());

        // Add the allocated block to the table, for the process
        table.get(pid).add(allocated);
        
        // The best fit block will have a hole of free memory left over
        if (bestFit.size() > size) {
            Range hole = new Range(bestFit.start() + size, bestFit.end());
            // Add the hole back into the table
            table.get(0).add(hole);
        }

        // Release the semaphore lock
        semaphore.signal();
    }

    /**
     * Release memory held by a process
     * @param pid
     * @throws InterruptedException 
     */
    public void free(int pid) throws InterruptedException {
        // Acquire the semaphore lock
        semaphore.waitSem();

        // Get the free memory allocation list
        ArrayList<Range> freeMemory = table.get(0);

        // Insert allocation list, if none exists
        table.putIfAbsent(pid, new ArrayList<Range>());
        // Get the processes memory allocation list
        ArrayList<Range> heldMemory = table.get(pid);

        // Check that the process is holding memory
        if (heldMemory.size() > 0) {
            // Remove each memory block from the process 
            for (int i=0; i<heldMemory.size(); i++) {
                // Sort the free memory so each block is in order
                // i.e. (0, 5), (5, 8), (8, 12)
                freeMemory.sort((a, b) -> Integer.compare(a.start(), b.start()));

                // A block of memory (no-longer) held by the process
                Range heldBlock = heldMemory.remove(i);
                
                // Go through each block of free memory and attempt to merge with released held memory
                for (int j=0; j<freeMemory.size() - 1; j++) {
                    // A block of free memory
                    Range freeBlock = freeMemory.get(j);

                    // No remaining free blocks can be connected to the held block
                    if (freeBlock.start() > heldBlock.start()) {
                        break;
                    }

                    // The held memory block is connected to a free memory block
                    if (freeBlock.end() == heldBlock.start() || heldBlock.end() == freeBlock.start()) {
                        // Merge the free block and the held block
                        Range mergedBlock = new Range(Math.min(freeBlock.start(), heldBlock.start()), Math.max(heldBlock.end(), freeBlock.end()));
                        
                        // Replace the free memory block with the two blocks merged
                        freeMemory.set(j, mergedBlock);

                        // Set held memory block to null, to indicate freed
                        heldBlock = null;
                        break;
                    }
                }

                // The held memory could not be merged with a free memory block
                if (heldBlock != null) {
                    // Add the held block into the free memory list
                    freeMemory.add(heldBlock);
                }
            }
        }

        // Release the semaphore lock
        semaphore.signal();
    }

    /**
     * Display the current memory layout
     */
    public void printMemory() {

    }
}