package black_jack.helper;

import black_jack.models.ReturnItem;
import black_jack.models.ReturnsForEpisode;
import black_jack.models.ValueMemory;

public class Learner {
    public static final double ALPHA_DEFAULT = 0.1;

    ValueMemory valueMemory; //reference
    double alpha = ALPHA_DEFAULT;

    public Learner(ValueMemory valueMemory) {
        this.valueMemory=valueMemory;
    }

    public Learner(ValueMemory valueMemory, double alpha) {
        this(valueMemory);
        this.alpha = alpha;
    }

    public void updateMemory(ReturnsForEpisode returns) {
        for (ReturnItem ri : returns.getReturns()) {
            updateMemory(ri);
        }
    }

    public void updateMemory(ReturnItem ri) {
        double oldValue = valueMemory.read(ri.state);
        double newValue = oldValue + alpha * (ri.returnValue - oldValue);
        valueMemory.write(ri.state, newValue);
    }

}
