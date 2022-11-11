package black_jack.helper;

import black_jack.models_cards.StateObservedActionObserved;
import black_jack.models_memory.NumberOfStateActionsVisitsMemory;
import black_jack.models_memory.StateActionValueMemory;
import black_jack.models_returns.ReturnItem;
import lombok.Getter;

@Getter
public class LearnerStateActionValue extends LearnerAbstract {

    StateActionValueMemory memory; //reference
    NumberOfStateActionsVisitsMemory numberOfStateActionsVisitsMemory;

    public LearnerStateActionValue(StateActionValueMemory memory,
                                   NumberOfStateActionsVisitsMemory numberOfStateActionsVisitsMemory) {
        super(LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
        this.memory = memory;
        this.numberOfStateActionsVisitsMemory = numberOfStateActionsVisitsMemory;
    }

    public LearnerStateActionValue(StateActionValueMemory memory,
                                   NumberOfStateActionsVisitsMemory numberOfStateVisitsMemory,
                                   double alpha,
                                   boolean regardNofVisitsFlag) {
        this(memory, numberOfStateVisitsMemory);
        this.alpha = alpha;
        this.regardNofVisitsFlag=regardNofVisitsFlag;
    }

    @Override
    public void increaseNofVisits(ReturnItem ri) {
        StateObservedActionObserved sa=new StateObservedActionObserved(ri.state,ri.cardAction);
        numberOfStateActionsVisitsMemory.increase(sa);
    }

    @Override
    public void updateMemory(ReturnItem ri) {
        StateObservedActionObserved sa=new StateObservedActionObserved(ri.state,ri.cardAction);
        double oldValue = memory.read(sa);
        int nofVisits= (int) numberOfStateActionsVisitsMemory.read(sa);
        double newValue = getNewValue(ri, oldValue,nofVisits);
        memory.write(sa, newValue);
    }


}
