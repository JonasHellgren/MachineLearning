package black_jack.helper;

import black_jack.models_cards.StateActionObserved;
import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;

public class LearnerStateActionValue implements LearnerInterface {

    StateActionValueMemory valueMemory; //reference
    NumberOfVisitsMemory numberOfVisitsMemory;
    double alpha = LearnerInterface.ALPHA_DEFAULT;
    boolean regardNofVisitsFlag= LearnerInterface.FLAG_DEFAULT;

    public LearnerStateActionValue(StateActionValueMemory valueMemory, NumberOfVisitsMemory numberOfVisitsMemory) {
        this.valueMemory=valueMemory;
        this.numberOfVisitsMemory=numberOfVisitsMemory;
    }

    public LearnerStateActionValue(StateActionValueMemory valueMemory,
                             NumberOfVisitsMemory numberOfVisitsMemory,
                             double alpha,
                             boolean regardNofVisitsFlag) {
        this(valueMemory,numberOfVisitsMemory);
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
        StateActionObserved sa=new StateActionObserved(ri.state,ri.cardAction);
        double oldValue = valueMemory.read(sa);
        double coeffNumberOfVisits=(regardNofVisitsFlag)
                ?1/(1 + numberOfVisitsMemory.read(ri.state))
                :1;
        double newValue = oldValue + alpha * coeffNumberOfVisits * (ri.returnValue - oldValue);
        valueMemory.write(sa, newValue);
    }


}
