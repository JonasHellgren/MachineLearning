package black_jack.helper;

import black_jack.models_memory.NumberOfStateVisitsMemory;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnItem;
import lombok.Setter;

@Setter
public class LearnerStateValue extends LearnerAbstract {
    StateValueMemory stateValueMemory; //reference

    public LearnerStateValue(StateValueMemory stateValueMemory,
                             NumberOfStateVisitsMemory numberOfStateVisitsMemory) {
        super(numberOfStateVisitsMemory,LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
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


    public void updateMemory(ReturnItem ri) {
        double oldValue = stateValueMemory.read(ri.state);
        double newValue = getNewValue(ri, oldValue);
        stateValueMemory.write(ri.state, newValue);
    }

}
