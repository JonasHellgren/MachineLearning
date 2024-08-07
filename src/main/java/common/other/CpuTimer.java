package common.other;

import lombok.Getter;
import java.util.logging.Logger;

@Getter
public class CpuTimer {

    static final Logger logger = Logger.getLogger(CpuTimer.class.getName());
    long startTimeMillis;  //starting time, long <=> minimum value of 0
    protected long timeBudgetMillis;

    long absoluteProgress;
    float relativeProgress;


    public CpuTimer() {
        this(Long.MAX_VALUE);
    }

    public static CpuTimer newWithTimeBudgetInMilliSec(long timeBudgetMillis) {
        return new CpuTimer(timeBudgetMillis);

    }

    public CpuTimer(long timeBudgetMillis) {
        this.timeBudgetMillis = timeBudgetMillis;
        reset();
    }

    public void reset() {
        startTimeMillis = System.currentTimeMillis();
    }

    public void stop() {
        absoluteProgress= absoluteProgressInMillis();
        relativeProgress=relativeProgress();
    }

    public boolean isTimeExceeded() {
        return System.currentTimeMillis() > startTimeMillis + timeBudgetMillis;
    }

    public float relativeProgress() {
        return absoluteProgressInMillis()/ (float) timeBudgetMillis;
    }

    public long absoluteProgressInMillis() {
        return  (System.currentTimeMillis() - startTimeMillis);
    }

    public String toString() {
        return "elapsed time in millis() = "+absoluteProgressInMillis()+", relativeProgress = "+relativeProgress();
    }

    public  String timeInMinutesAsString() {
        double timeInMin = absoluteProgressInMillis() * 1d / 1000 / 60 ;
        return "Time (minutes) = "  + timeInMin;
    }

    public  String timeInSecondsAsString() {
        double timeInSec = absoluteProgressInMillis() * 1d / 1000 ;
        return String.valueOf(timeInSec);
    }


}
