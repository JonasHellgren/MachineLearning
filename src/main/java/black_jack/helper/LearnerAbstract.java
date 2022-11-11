package black_jack.helper;

import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
abstract public class LearnerAbstract implements LearnerInterface {


    double alpha;
    boolean regardNofVisitsFlag;

    public abstract void updateMemory(ReturnItem ri);
    public abstract void increaseNofVisits(ReturnItem ri);

    @Override
    public void updateMemoryFromEpisodeReturns(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
            if (regardNofVisitsFlag) {
                increaseNofVisits(ri);
            }
        }
    }

    double getNewValue(ReturnItem ri, double oldValue, int nofVisits) {
        double coeffNumberOfVisits=(regardNofVisitsFlag)
                ?1/(float) (1 + nofVisits)
                :1;
        return oldValue + alpha * coeffNumberOfVisits * (ri.returnValue - oldValue);
    }

}
