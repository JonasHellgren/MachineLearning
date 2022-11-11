package black_jack.helper;

import black_jack.models_cards.StateActionObserved;
import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;

public class LearnerStateActionValue extends LearnerAbstract {

    StateActionValueMemory valueMemory; //reference

    public LearnerStateActionValue(StateActionValueMemory valueMemory,
                                   NumberOfVisitsMemory numberOfVisitsMemory) {
        super(numberOfVisitsMemory,LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
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
    public void updateMemory(ReturnItem ri) {
        StateActionObserved sa=new StateActionObserved(ri.state,ri.cardAction);
        double oldValue = valueMemory.read(sa);
        double newValue = getNewValue(ri, oldValue);
        valueMemory.write(sa, newValue);
    }


}
