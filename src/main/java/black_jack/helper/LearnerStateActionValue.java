package black_jack.helper;

import black_jack.models_cards.StateActionObserved;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnItem;

public class LearnerStateActionValue extends LearnerAbstract {

    StateActionValueMemory valueMemory; //reference

    public LearnerStateActionValue(StateActionValueMemory valueMemory,
                                   NumberOfStateVisitsMemory numberOfStateVisitsMemory) {
        super(numberOfStateVisitsMemory,LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
        this.valueMemory=valueMemory;
        this.numberOfStateVisitsMemory = numberOfStateVisitsMemory;
    }

    public LearnerStateActionValue(StateActionValueMemory valueMemory,
                                   NumberOfStateVisitsMemory numberOfStateVisitsMemory,
                                   double alpha,
                                   boolean regardNofVisitsFlag) {
        this(valueMemory, numberOfStateVisitsMemory);
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
