package policy_gradient_problems.common_helpers;

import org.apache.commons.math3.linear.ArrayRealVector;

/**
 * The critic in some actor-critic agents
 */

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


    public void updateFromExperience(int intState, double delta, Double beta) {
        double valueOld=getValue(intState);
        setValue(intState,valueOld+ beta * delta);
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
