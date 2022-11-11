package black_jack.helper;

import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;
import lombok.Setter;

@Setter
public class LearnerStateValue implements LearnerInterface {
    StateValueMemory stateValueMemory; //reference
    NumberOfVisitsMemory numberOfVisitsMemory;
    double alpha = LearnerInterface.ALPHA_DEFAULT;
    boolean regardNofVisitsFlag= LearnerInterface.FLAG_DEFAULT;

    public LearnerStateValue(StateValueMemory stateValueMemory, NumberOfVisitsMemory numberOfVisitsMemory) {
        this.stateValueMemory = stateValueMemory;
        this.numberOfVisitsMemory=numberOfVisitsMemory;
    }

    public LearnerStateValue(StateValueMemory stateValueMemory,
                             NumberOfVisitsMemory numberOfVisitsMemory,
                             double alpha,
                             boolean regardNofVisitsFlag) {
        this(stateValueMemory,numberOfVisitsMemory);
        this.alpha = alpha;
        this.regardNofVisitsFlag=regardNofVisitsFlag;
    }

    @Override
    public void updateMemory(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
            if (regardNofVisitsFlag) {
                numberOfVisitsMemory.increase(ri.state);
            }
        }
    }

    @Override
    public void updateMemory(ReturnItem ri) {
        double oldValue = stateValueMemory.read(ri.state);
        double coeffNumberOfVisits=(regardNofVisitsFlag)
                            ?1/(1 + numberOfVisitsMemory.read(ri.state))
                            :1;
        double newValue = oldValue + alpha * coeffNumberOfVisits * (ri.returnValue - oldValue);
        stateValueMemory.write(ri.state, newValue);
    }

}
