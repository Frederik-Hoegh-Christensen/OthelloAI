import java.util.ArrayList;
import java.util.Date;

public class Timer {
    public static ArrayList<Long> times = new ArrayList<>();

    public static long addTime(Date start, Date end) {
        long diff = end.getTime() - start.getTime();
        times.add(diff);
        return diff;
    }
    
    public static long getAverage() {
        long sum = 0;
        for (long time : times) {
            sum += time;
        }
        return sum / times.size();
    }
}