package policy_gradient_problems.common;

import org.apache.commons.math3.linear.ArrayRealVector;

public class TabularValueFunction {
    ArrayRealVector wVector;   //value function parameters
    int nofNonTerminalStates;

    public TabularValueFunction(int nofNonTerminalStates) {
        this.nofNonTerminalStates=nofNonTerminalStates;
        this.wVector=getZeroVector();
    }

    public double getValue(int state) {
     return getZeroVectorWithSingleElementAsOne(state).dotProduct(wVector);
    }

    public void setValue(int state, double value) {
        wVector.setEntry(state,value);
    }

    public void updateFromExperience(Experience experience, double delta, Double beta) {
        double valueOld=getValue(experience.state());
        setValue(experience.state(),valueOld+ beta * delta);
    }

    private  ArrayRealVector getZeroVector() {
        return new ArrayRealVector(nofNonTerminalStates);
    }

    private  ArrayRealVector getZeroVectorWithSingleElementAsOne(int indexOneElement) {
        var vector=getZeroVector();
        vector.setEntry(indexOneElement,1d);
        return vector;
    }



}
