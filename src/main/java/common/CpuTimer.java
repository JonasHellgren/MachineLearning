package common;

import lombok.Getter;
import lombok.NonNull;

import java.util.logging.Logger;

@Getter
public class CpuTimer {

    static final Logger logger = Logger.getLogger(CpuTimer.class.getName());
    long startTimeMillis;  //starting time, long <=> minimum value of 0
    protected long timeBudgetMillis;

    long absoluteProgress;
    double relativeProgress;

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

    public double relativeProgress() {
        return absoluteProgress()/ (double) timeBudgetMillis;
    }

    public long absoluteProgress() {
        return  (System.currentTimeMillis() - startTimeMillis);
    }

}
