import java.util.ArrayList;
import java.util.HashMap;

/**
 * What I'm currently thinking would be best to implement MemoryManager, is to store each RAM
 * allocation (Block) as a list within a HashMap, per each PID, with 0 being free. If the client 
 * allocates more RAM to a process than currently free, release the memory in-use, and wait.
 */
public class MemoryManager {
    // Number of rows to be displayed
    private static int ROWS = 10;

    // Used a fixed-size array to simulate memory blocks
    private int[] memory;
    private HashMap<Integer, ArrayList<Block>> table;
    private Semaphore2 semaphore = new Semaphore2();

    public MemoryManager(int maxBytes) throws InterruptedException {
        // Acquire the semaphore lock
        semaphore.waitSem();

        // Initialize the memory
        memory = new int[maxBytes];

        // Initialize the HashMap<PID, List<Block>>
        table = new HashMap<>();

        // Add the free memory (PID == 0)
        table.putIfAbsent(0, new ArrayList<Block>());
        // Record the initial free memory
        table.get(0).add(new Block(0, maxBytes));

        // Release the semaphore lock
        semaphore.signal();
    }

    /**
     * Get the total memory held by a process (PID 0 == free memory)
     * For proper practices, should only be called within a function holding the semaphore lock
     * @param pid The process ID
     * @return Number of bytes held by the process
     */
    private int getTotalMemory(int pid) {
        int total = 0;

        // Add the size of each block
        for (Block block : table.get(pid)) {
            total += block.size();
        }

        // Return the sum
        return total;
    }

    /**
     * Point a block of memory to a process
     * For proper practices should only be called within a function holding the semaphore lock
     * @param pid The process ID
     * @param block The memory block
     */
    private void setBlock(int pid, Block block) {
        // Go through each byte in the block
        for (int i=block.start(); i<block.end(); i++) {
            // Set the byte held by PID
            memory[i] = pid;
        }
    }

    /**
     * Use First-Fit, Best-Fit, or Worst-Fit to find
     * a free region and store the PID in that region
     * @param pid The process ID
     * @param size The processes memory size
     * @throws InterruptedException 
     */
    public boolean allocate(int pid, int size) throws InterruptedException {
        // The allocation request is too large
        if (size > memory.length) {
            System.out.println("Cannot fullfill allocation request: Memory Index Out of Bounds");
            return false;
        }

        // Acquire the semaphore lock
        semaphore.waitSem();

        // There isn't enough available memory to allocate
        if (getTotalMemory(0) < size) {
            System.out.println("Failed to allocate memory: Out of Memory");

            // Release the semaphore lock
            semaphore.signal();
            return false;
        }

        // Get all blocks of free memory
        ArrayList<Block> freeMemory = table.get(0);
        
        // Create the allocation list for the process
        table.putIfAbsent(pid, new ArrayList<Block>());
        
        // Store the best fitting Block, for this allocation
        // Initially none
        Block bestFit = null;

        // Go through each block of free memory
        for (Block freeBlock : freeMemory) {
            // The block is large enough, and is the smallest block yet found
            if (freeBlock.size() >= size && (bestFit == null || freeBlock.size() < bestFit.size())) {
                // Save the smallest available block
                bestFit = freeBlock;
            }
        }
        
        // No single best-fit block was found
        // Find however many blocks, from largest to smallest needed to allocate memory
        if (bestFit == null) {
            // Sort the free memory by size; largest blocks to smallest blocks
            freeMemory.sort((a, b) -> Integer.compare(b.size(), a.size()));
            
            // The number of blocks that will be taken from available memory to allocate the requested size
            // Sublist of freeMemory from index 0 to 'blocks - 1'
            int blocks = 0;
            // The combined size of the blocks to be taken
            int totalSize = 0;

            // Iterate in order, by largest to smallest block
            for (Block block : freeMemory) {
                // Increment number of blocks by 1
                blocks += 1;

                // Increment total size by block size
                totalSize += block.size();

                // The memory allocation has been found
                if (totalSize >= size) {
                    break;
                }
            }
            
            // Remove the last block
            Block lastBlock = freeMemory.remove(blocks - 1);
            
            // Allocate all blocks that leave no holes
            for (int i=0; i<blocks - 1; i++) {
                // Remove the block from available memory
                Block block = freeMemory.remove(i);
                // Add the block to the processes held memory
                table.get(pid).add(block);
                // Change the memory list to reflect the held memory block
                setBlock(pid, block);
            }

            // There is a hole left over
            if (totalSize - size > 0) {
                // Add the hole back to the free memory
                Block hole = new Block(lastBlock.end() - (totalSize - size), lastBlock.end());
                freeMemory.add(hole);
            }

            // The final block to be allocated to the process
            Block lastAllocation = new Block(lastBlock.start(), lastBlock.end() - (totalSize - size));
            
            // Add the last allocation block to the held memory
            table.get(pid).add(lastAllocation);

            // Change the memory list to reflect the held memory block
            setBlock(pid, lastAllocation);


            semaphore.signal();

            return true;
        }

        // Remove the best-fit block from the table
        // because we will break it into two blocks
        table.get(0).remove(bestFit);

        // Create the smallest block to hold the allocation.
        Block allocated = new Block(bestFit.start(), bestFit.start() + size);

        // Add the allocated block to the table for the process
        table.get(pid).add(allocated);

        // Change the memory list to reflect the held memory block
        setBlock(pid, allocated);
        
        // The best fit block will have a hole of free memory left over
        if (bestFit.size() > size) {
            Block hole = new Block(bestFit.start() + size, bestFit.end());
            // Add the hole back into the table
            table.get(0).add(hole);
        }

        // Release the semaphore lock
        semaphore.signal();

        return true;
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
        ArrayList<Block> freeMemory = table.get(0);

        // Insert allocation list, if none exists
        table.putIfAbsent(pid, new ArrayList<Block>());
        // Get the processes memory allocation list
        ArrayList<Block> heldMemory = table.get(pid);

        // The process isn't holding any memory
        if (heldMemory.size() == 0) {
            // Release the semaphore lock
            semaphore.signal();
            return;
        }

        // Remove each memory block from the process 
        for (int i=0; i<heldMemory.size(); i++) {
            // Sort the free memory so each block is in order
            // i.e. (0, 5), (5, 8), (8, 12)
            freeMemory.sort((a, b) -> Integer.compare(a.start(), b.start()));

            // A block of memory (no-longer) held by the process
            Block heldBlock = heldMemory.remove(i);

            // Change the memory list to reflect the held memory as freed
            setBlock(0, heldBlock);
            
            // Go through each block of free memory and attempt to merge with released held memory
            for (int j=0; j<freeMemory.size() - 1; j++) {
                // A block of free memory
                Block freeBlock = freeMemory.get(j);

                // No remaining free blocks can be connected to the held block
                if (freeBlock.start() > heldBlock.start()) {
                    break;
                }

                // The held memory block is connected to a free memory block
                if (freeBlock.end() == heldBlock.start() || heldBlock.end() == freeBlock.start()) {
                    // Merge the free block and the held block
                    Block mergedBlock = new Block(Math.min(freeBlock.start(), heldBlock.start()), Math.max(heldBlock.end(), freeBlock.end()));
                    
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

        System.out.println("Terminated process: PID " + pid);

        // Release the semaphore lock
        semaphore.signal();
    }

    /**
     * Check if a process has memory allocated to it
     * @param pid The process ID
     * @return True if a process has allocated memory; False, otherwise
     * @throws InterruptedException 
     */
    public boolean hasAllocation(int pid) throws InterruptedException {
        try {
            // Acquire the semaphore lock
            semaphore.waitSem();

            // Return the allocation comparison
            return getTotalMemory(pid) > 0;
        }
        catch (Exception e) {
            return false;
        }   
        finally {
            // Release the semaphore lock
            semaphore.signal();
        }
    }

    /**
     * Display the current memory layout
     */
    public void printMemory() {
        // The number of bytes of memory
        int size = memory.length;
        int columns = size / MemoryManager.ROWS;

        // Go through each byte, displayed in rows
        for (int i=0; i<MemoryManager.ROWS; i++) {
            // Go through each byte within the row
            for (int j=i * columns; j<i * columns + columns; j++) {
                System.out.print(" " + memory[j]);
            }
            // Print the new line
            System.out.println();
        }
    }
}