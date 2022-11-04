package black_jack.helper;

import black_jack.models.NumberOfVisitsMemory;
import black_jack.models.ReturnItem;
import black_jack.models.ReturnsForEpisode;
import black_jack.models.ValueMemory;

public class Learner {
    public static final double ALPHA_DEFAULT = 0.1;
    private static final boolean FLAG_DEFAULT=true;

    ValueMemory valueMemory; //reference
    NumberOfVisitsMemory numberOfVisitsMemory;
    double alpha = ALPHA_DEFAULT;
    boolean regardNofVisitsFlag= FLAG_DEFAULT;

    public Learner(ValueMemory valueMemory, NumberOfVisitsMemory numberOfVisitsMemory) {
        this.valueMemory=valueMemory;
        this.numberOfVisitsMemory=numberOfVisitsMemory;
    }

    public Learner(ValueMemory valueMemory,
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
