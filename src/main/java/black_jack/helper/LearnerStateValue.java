package black_jack.helper;

import black_jack.models_cards.StateObservedActionObserved;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnItem;
import lombok.Setter;

@Setter
public class LearnerStateValue extends LearnerAbstract {
    StateValueMemory stateValueMemory; //reference
    NumberOfStateVisitsMemory numberOfStateVisitsMemory;

    public LearnerStateValue(StateValueMemory stateValueMemory,
                             NumberOfStateVisitsMemory numberOfStateVisitsMemory) {
        super(LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
        this.stateValueMemory = stateValueMemory;
        this.numberOfStateVisitsMemory = numberOfStateVisitsMemory;
    }

    public LearnerStateValue(StateValueMemory stateValueMemory,
                             NumberOfStateVisitsMemory numberOfStateVisitsMemory,
                             double alpha,
                             boolean regardNofVisitsFlag) {
        this(stateValueMemory, numberOfStateVisitsMemory);
        this.alpha = alpha;
        this.regardNofVisitsFlag=regardNofVisitsFlag;
    }

    @Override
    public void increaseNofVisits(ReturnItem ri) {
        numberOfStateVisitsMemory.increase(ri.state);
    }

    @Override
    public void updateMemory(ReturnItem ri) {
        StateObservedActionObserved sa=new StateObservedActionObserved(ri.state,ri.cardAction);
        double oldValue = stateValueMemory.read(ri.state);
        int nofVisits= (int) numberOfStateVisitsMemory.read(ri.state);
        double newValue = getNewValue(ri, oldValue,nofVisits);
        stateValueMemory.write(ri.state, newValue);
    }


}
