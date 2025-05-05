public class Semaphore {

    private boolean[] buffer;

    // Semaphore with a single lock
    public Semaphore() {
        buffer = new boolean[1];
    }

    // Semaphore with multiple locks
    public Semaphore(int number) {
        buffer = new boolean[number];
    }

    /**
     * Acquire/Wait-For the semaphore lock
     */
    public synchronized void waitSem() throws InterruptedException {
        while (buffer[0]) {
            wait();
        }
        // Lock the semaphore
        buffer[0] = true;
    }

    /**
     * Acquire/Wait-For a specific lock
     * @param number The lock to acquire
     */
    public synchronized void waitSem(int number) throws InterruptedException {
        // Check the input
        if (number < 0 || number >= buffer.length) {
            throw new IllegalArgumentException("Invalid lock number");
        }

        // Wait for the lock
        while (buffer[number]) {
            wait();
        }
        // Lock the semaphore
        buffer[number] = true;
    }

    /**
     * Release the semaphore lock
     */
    public synchronized void signal() {
        // Release the semaphore lock
        buffer[0] = false;
        // Notify waiting threads
        notifyAll();
    }

    /**
     * Release a specific lock
     * @param number The lock to release
     */
    public synchronized void signal(int number) {
        // Check the input
        if (number < 0 || number >= buffer.length) {
            throw new IllegalArgumentException("Invalid lock number");
        }
        
        // Release the semaphore lock on index 'number'
        buffer[number] = false;
        // Notify waiting threads
        notifyAll();
    }
}
