package black_jack.helper;

import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
abstract public class LearnerAbstract implements LearnerInterface {

    NumberOfStateVisitsMemory numberOfStateVisitsMemory;
    double alpha;
    boolean regardNofVisitsFlag;

    public abstract void updateMemory(ReturnItem ri);

    @Override
    public void updateMemoryFromEpisodeReturns(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
            if (regardNofVisitsFlag) {
                numberOfStateVisitsMemory.increase(ri.state);
            }
        }
    }

    double getNewValue(ReturnItem ri, double oldValue) {
        double coeffNumberOfVisits=(regardNofVisitsFlag)
                ?1/(1 + numberOfStateVisitsMemory.read(ri.state))
                :1;
        return oldValue + alpha * coeffNumberOfVisits * (ri.returnValue - oldValue);
    }

}
