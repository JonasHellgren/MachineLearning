package safe_rl.helpers;

import com.beust.jcommander.internal.Lists;
import common.list_arrays.ListUtils;
import common.other.Conditionals;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Log
public class LossTracker {

    List<Double> meanLosses= Lists.newArrayList();
    List<Double> stdLosses=Lists.newArrayList();
    List<Double> criticLosses=Lists.newArrayList();

    public void addMeanAndStdLoss(double mean, double std) {
        meanLosses.add(mean);
        stdLosses.add(std);
    }

    public void addCriticLoss(double criticLoss) {
        criticLosses.add(criticLoss);
    }

    public double averageMeanLosses() {
        logIfEmpty();
        return ListUtils.findAverage(meanLosses).orElse(0);
    }

    public double averageStdLosses() {
        logIfEmpty();
        return ListUtils.findAverage(stdLosses).orElse(0);
    }

    private void logIfEmpty() {
        Conditionals.executeIfTrue(meanLosses.isEmpty() || stdLosses.isEmpty(),
                () -> log.warning("meanLosses/stdLosses is empty"));
    }

    public double averageCriticLosses() {
        Conditionals.executeIfTrue(criticLosses.isEmpty(),
                () -> log.warning("Critic losses is empty"));
        return ListUtils.findAverage(criticLosses).orElse(0);
    }


    public void clearActorLosses() {
        meanLosses.clear();
        stdLosses.clear();
    }

    public void clearCriticLosses() {
        criticLosses.clear();
    }

}
