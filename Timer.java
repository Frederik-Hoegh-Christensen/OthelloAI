import java.util.ArrayList;
import java.util.Date;
/**
 * The timer class is a utility class containing methods for storing the timing of our AI's searches
 * and getting the average, min and max search times.
 */
public class Timer {
    private ArrayList<Long> times = new ArrayList<>();
    private long maxTime = Integer.MIN_VALUE;
    private long minTime = Integer.MAX_VALUE;

    public long addTime(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        maxTime = diff > maxTime ? diff : maxTime;
        minTime = diff < minTime ? diff : minTime;
        times.add(diff);
        return diff;
    }
    
    public long getAverage() {
        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        return sum / times.size();
    }

    public long getMinTime() {
        return minTime;
    }

    public long getMaxTime() {
        return maxTime;
    }
    
}