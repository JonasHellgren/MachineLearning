package black_jack.helper;

import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
abstract public class LearnerAbstract implements LearnerInterface {

    NumberOfVisitsMemory numberOfVisitsMemory;
    double alpha;
    boolean regardNofVisitsFlag;

    public abstract void updateMemory(ReturnItem ri);

    @Override
    public void updateMemoryFromEpisodeReturns(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
            if (regardNofVisitsFlag) {
                numberOfVisitsMemory.increase(ri.state);
            }
        }
    }

    double getNewValue(ReturnItem ri, double oldValue) {
        double coeffNumberOfVisits=(regardNofVisitsFlag)
                ?1/(1 + numberOfVisitsMemory.read(ri.state))
                :1;
        return oldValue + alpha * coeffNumberOfVisits * (ri.returnValue - oldValue);
    }

}
