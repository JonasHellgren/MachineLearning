package java_ai_gym.models_common;

import java_ai_gym.swing.FrameEnvironment;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;

public abstract class  Environment {

    public class PolicyTestReturn {
        public double successRatio;
        public double avgNofSteps;
        public double maxQaverage;
        public double bellmanErrAverage;
    }

    public class RunPolicyReturn {
        public double avgMaxQ;
        public double avgBellmannErr;
    }

    protected FrameEnvironment animationFrame;
    protected FrameEnvironment plotFrame;

    protected abstract StepReturn step(int action, State state);
    protected abstract boolean isTerminalState(State state);
    protected abstract boolean isTerminalStatePolicyTest(State state);
    protected abstract boolean isPolicyTestSuccessful(State state);
    protected abstract void setRandomStateValuesStart(State state);

    protected double clip(double variable, double minValue, double maxValue) {
        double lowerThanMax= Math.min(variable, maxValue);
        return Math.max(lowerThanMax, minValue);
    }

    protected boolean isZero(double value) {
        return (Math.abs(value-0)<2*Double.MIN_VALUE);
    }

    public PolicyTestReturn testPolicy(AgentNeuralNetwork agent,EnvironmentParametersAbstract parameters, int nofTest) {

        List<Integer> nofStepsList = new ArrayList<>();
        List<Double> maxQaverageList = new ArrayList<>();
        List<Double> bellmanErrList = new ArrayList<>();
        int nofSuccessTests = 0;
        for (int i = 0; i < nofTest; i++) {
            setRandomStateValuesStart(agent.state);
            RunPolicyReturn runPolicyReturn=runPolicy(agent, parameters);
            int nofSteps = agent.state.getDiscreteVariable("nofSteps");
            nofStepsList.add(nofSteps);
            maxQaverageList.add(runPolicyReturn.avgMaxQ);
            bellmanErrList.add(runPolicyReturn.avgBellmannErr);
            if (isPolicyTestSuccessful(agent.state))
                nofSuccessTests++;
        }

        PolicyTestReturn policyTestReturn=new PolicyTestReturn();
        IntSummaryStatistics statsNofSteps = nofStepsList.stream().mapToInt(a -> a).summaryStatistics();
        DoubleSummaryStatistics statsMaxQ = maxQaverageList.stream().mapToDouble(a -> a).summaryStatistics();
        DoubleSummaryStatistics statsBellmanErr = bellmanErrList.stream().mapToDouble(a -> a).summaryStatistics();
        policyTestReturn.avgNofSteps=statsNofSteps.getAverage();
        policyTestReturn.successRatio=nofSuccessTests / (double) nofTest;
        policyTestReturn.maxQaverage=statsMaxQ.getAverage();
        policyTestReturn.bellmanErrAverage=statsBellmanErr.getAverage();

        return policyTestReturn;
    }

    protected RunPolicyReturn runPolicy(AgentNeuralNetwork agent, EnvironmentParametersAbstract parameters) {
        StepReturn stepReturn;
        List<Double> maxQList = new ArrayList<>();
        List<Double> bellmanErrList = new ArrayList<>();

        int tempTotalNofSteps=agent.state.totalNofSteps;  //evaluation shall not affect totalNofSteps
        do {
            stepReturn=step(agent.chooseBestAction(agent.state, parameters),agent.state);
            agent.state.copyState(stepReturn.state);

            INDArray inputNetwork = agent.setNetworkInput(agent.state, parameters);
            double qOld = agent.readMemory(inputNetwork, agent.chooseBestAction(agent.state, parameters));
            double bellmanErrorStep=agent.calcBellmanErrorStep(stepReturn, qOld, parameters);

            maxQList.add(agent.findMaxQTargetNetwork(agent.state,parameters));
            bellmanErrList.add(Math.abs(bellmanErrorStep));
        } while (!isTerminalStatePolicyTest(agent.state));
        agent.state.totalNofSteps=tempTotalNofSteps;

        RunPolicyReturn runPolicyReturn=new RunPolicyReturn();
        DoubleSummaryStatistics statsMaxQ = maxQList.stream().mapToDouble(a -> a).summaryStatistics();
        runPolicyReturn.avgMaxQ=statsMaxQ.getAverage();
        DoubleSummaryStatistics statsBellmanErr = bellmanErrList.stream().mapToDouble(a -> a).summaryStatistics();
        runPolicyReturn.avgBellmannErr=statsBellmanErr.getAverage();
        return runPolicyReturn;
    }

}
