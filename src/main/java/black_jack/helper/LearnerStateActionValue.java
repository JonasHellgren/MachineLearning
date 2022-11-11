package black_jack.helper;

import black_jack.models_cards.StateActionObserved;
import black_jack.models_memory.NumberOfStateActionsVisitsMemory;
import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnItem;
import lombok.Getter;

@Getter
public class LearnerStateActionValue extends LearnerAbstract {

    StateActionValueMemory valueMemory; //reference
    NumberOfStateActionsVisitsMemory numberOfStateActionsVisitsMemory;

    public LearnerStateActionValue(StateActionValueMemory valueMemory,
                                   NumberOfStateActionsVisitsMemory numberOfStateActionsVisitsMemory) {
        super(LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
        this.valueMemory=valueMemory;
        this.numberOfStateActionsVisitsMemory = numberOfStateActionsVisitsMemory;
    }

    public LearnerStateActionValue(StateActionValueMemory valueMemory,
                                   NumberOfStateActionsVisitsMemory numberOfStateVisitsMemory,
                                   double alpha,
                                   boolean regardNofVisitsFlag) {
        this(valueMemory, numberOfStateVisitsMemory);
        this.alpha = alpha;
        this.regardNofVisitsFlag=regardNofVisitsFlag;
    }

    @Override
    public void increaseNofVisits(ReturnItem ri) {
        StateActionObserved sa=new StateActionObserved(ri.state,ri.cardAction);
        numberOfStateActionsVisitsMemory.increase(sa);
    }

    @Override
    public void updateMemory(ReturnItem ri) {
        StateActionObserved sa=new StateActionObserved(ri.state,ri.cardAction);
        double oldValue = valueMemory.read(sa);
        int nofVisits= (int) numberOfStateActionsVisitsMemory.read(sa);
        double newValue = getNewValue(ri, oldValue,nofVisits);
        valueMemory.write(sa, newValue);
    }


}
