import java.util.Scanner;

public class Main {
    /**
     * Create a process
     * @param name The process name
     */
    public void create(String name) {

    }

    /**
     * Allocate memory to a process
     * @param pid The process ID
     * @param size The memory to allocate
     */
    public void allocate(int pid, int size) {

    }

    public static void main(String[] args) {
        // Create commandline input scanner
        Scanner input = new Scanner(System.in);

        // Continue running as long as the input has a new line
        while (input.hasNextLine()) {
            // The inputted command
            String line = input.nextLine();

            // Check the first word against the commands list
            // Ignores invalid casing
            switch(line.split(" ")[0].toLowerCase()) {
                // Create a new process
                case("create"):

                    break;
                // Display processes information
                case("ps"):

                    break;
                // Display process schedule information
                case("schedule"):

                    break;
                // Allocate memory to a process
                case("alloc"):

                    break;
                // Display memory information
                case("mem"):

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