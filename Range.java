/**
 * Created a Range class to represent indexed memory blocks.
 */
public class Range {
    private int start, end;

    /**
     * A range to hold the start/end indices
     * i.e. Range(0, 10) should cover from index 0 up to & including index 9
     * @param start The start index
     * @param end The end index (exclusive)
     */
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int start() {
        return this.start;
    }

    public int end() {
        return this.end;
    }

    /**
     * Get the size of the range
     * @return End - Start
     */
    public int size() {
        return this.end - this.start;
    }
}
