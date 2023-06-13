package common;

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

    public static CpuTimer newTimer(long timeBudgetMillis) {
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
        absoluteProgress=absoluteProgress();
        relativeProgress=relativeProgress();
    }

    public boolean isTimeExceeded() {
        return System.currentTimeMillis() > startTimeMillis + timeBudgetMillis;
    }

    public float relativeProgress() {
        return absoluteProgress()/ (float) timeBudgetMillis;
    }

    public long absoluteProgress() {
        return  (System.currentTimeMillis() - startTimeMillis);
    }

    public String toString() {
        return "timeBudgetMillis = "+timeBudgetMillis+", relativeProgress = "+relativeProgress;
    }

}
