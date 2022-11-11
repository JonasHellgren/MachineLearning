package black_jack.helper;

import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_memory.StateValueMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;
import lombok.Setter;

@Setter
public class LearnerStateValue extends LearnerAbstract {
    StateValueMemory stateValueMemory; //reference

    public LearnerStateValue(StateValueMemory stateValueMemory,
                             NumberOfVisitsMemory numberOfVisitsMemory) {
        super(numberOfVisitsMemory,LearnerInterface.ALPHA_DEFAULT,LearnerInterface.FLAG_DEFAULT);
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


    public void updateMemory(ReturnItem ri) {
        double oldValue = stateValueMemory.read(ri.state);
        double newValue = getNewValue(ri, oldValue);
        stateValueMemory.write(ri.state, newValue);
    }

}
