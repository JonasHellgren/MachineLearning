package black_jack.helper;

import black_jack.models_memory.NumberOfVisitsMemory;
import black_jack.models_returns.ReturnItem;
import black_jack.models_returns.ReturnsForEpisode;
import black_jack.models_memory.ValueMemory;
import lombok.Setter;

@Setter
public class LearnerValue {
    public static final double ALPHA_DEFAULT = 0.1;
    private static final boolean FLAG_DEFAULT=true;

    ValueMemory valueMemory; //reference
    NumberOfVisitsMemory numberOfVisitsMemory;
    double alpha = ALPHA_DEFAULT;
    boolean regardNofVisitsFlag= FLAG_DEFAULT;

    public LearnerValue(ValueMemory valueMemory, NumberOfVisitsMemory numberOfVisitsMemory) {
        this.valueMemory=valueMemory;
        this.numberOfVisitsMemory=numberOfVisitsMemory;
    }

    public LearnerValue(ValueMemory valueMemory,
                        NumberOfVisitsMemory numberOfVisitsMemory,
                        double alpha,
                        boolean regardNofVisitsFlag) {
        this(valueMemory,numberOfVisitsMemory);
        this.alpha = alpha;
        this.regardNofVisitsFlag=regardNofVisitsFlag;
    }

    public void updateMemory(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
            if (regardNofVisitsFlag) {
                numberOfVisitsMemory.increase(ri.state);
            }

        }
    }

    public void updateMemory(ReturnItem ri) {
        double oldValue = valueMemory.read(ri.state);
        double coeffNumberOfVisits=(regardNofVisitsFlag)
                            ?1/(1 + numberOfVisitsMemory.read(ri.state))
                            :1;
        double newValue = oldValue + alpha * coeffNumberOfVisits * (ri.returnValue - oldValue);
        valueMemory.write(ri.state, newValue);
    }

}
