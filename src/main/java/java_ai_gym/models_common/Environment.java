package java_ai_gym.models_common;

import java_ai_gym.models_mountaincar.MountainCar;
import java_ai_gym.models_mountaincar.MountainCarAgentNeuralNetwork;
import java_ai_gym.swing.FrameEnvironment;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.List;

public abstract class  Environment {

    public int NOF_EPISODES_BETWEEN_POLICY_TEST=10;
    public int NOF_TESTS_WHEN_TESTING_POLICY=10;

    public class GraphicsSettings  {
        public final  int FRAME_WEIGHT =600;
        public final  int FRAME_HEIGHT =300;
        public final int FRAME_MARGIN =50;  //frame margin
        public final int LABEL_WEIGHT =FRAME_WEIGHT/2;
        public final int LABEL_HEIGHT =15;
        public final int LABEL_XPOS =10;
        public final int LABEL_XPOSY_MIN =0;
        public int NOF_DOTS_PLOTTED_POLICY =1000;
    }


    private State templateState=new State();

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
    public GraphicsSettings gfxSettings =new GraphicsSettings();

    protected abstract StepReturn step(int action, State state);
    protected abstract boolean isTerminalState(State state);
    protected abstract boolean isTerminalStatePolicyTest(State state);
    protected abstract boolean isPolicyTestSuccessful(State state);
    protected abstract void setRandomStateValuesStart(State state);
    public abstract void render(AgentNeuralNetwork agent, int action);
    public abstract void createVariablesInState(State state) ;

    public State getTemplateState() {
        return templateState;
    }

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

    public boolean isTimeForPolicyTest(int iEpisode) {
       return (iEpisode % NOF_EPISODES_BETWEEN_POLICY_TEST == 0 | iEpisode == 0);
    }

    public void printPolicyTest(int iEpisode, AgentNeuralNetwork agent, PolicyTestReturn policyTestReturn, int MAX_NOF_STEPS_POLICY_TEST) {

        System.out.printf("iEpisode: %d ",iEpisode);
        System.out.printf(", success ratio: %.2f", policyTestReturn.successRatio);
        System.out.printf(", avg of maxQ: %.2f ", policyTestReturn.maxQaverage);
        System.out.printf(", avg bellman err: %.2f", policyTestReturn.bellmanErrAverage);
        System.out.printf(", avg(NofSteps) div maxNofsteps: %.2f", policyTestReturn.avgNofSteps/MAX_NOF_STEPS_POLICY_TEST);
        System.out.printf(", learning rate: %.5f", agent.calcLearningRate(agent.calcFractionEpisodes(iEpisode)));
        System.out.printf(", prob random action: %.5f", agent.calcProbRandAction(agent.calcFractionEpisodes(iEpisode)));
        System.out.println();

    }

    public void simulateEpisode(AgentNeuralNetwork agent, int iEpisode,Environment env,EnvironmentParametersAbstract envParams) {
        // Q learning equation: Q[s][a] = Q[s][a] + alpha ( R[s][a] + gamma (max Q[sNew]) - Q[s][a] )
        // a single episode: the agent finds a path from state s to the exit state

        StepReturn stepReturn;
        State sNew = new State();
        double fEpisodes=agent.calcFractionEpisodes(iEpisode);

        do {
            int aChosen=agent.chooseAction(fEpisodes,envParams);
            stepReturn = env.step(aChosen, agent.state);
            Experience experience = new Experience(new State(agent.state), aChosen, stepReturn, agent.BE_ERROR_INIT);
            agent.replayBuffer.addExperience(experience);

            if (agent.replayBuffer.isFull(agent) & agent.isItTimeToFit() ) {
                List<Experience> miniBatch =
                        agent.replayBuffer.getMiniBatchPrioritizedExperienceReplay(
                                agent.MINI_BATCH_SIZE,
                                agent.calcFractionEpisodes(iEpisode));

                agent.fitFromMiniBatch(miniBatch, envParams,fEpisodes);
                agent.maybeUpdateTargetNetwork();
            }

            sNew.copyState(stepReturn.state);
            agent.state.copyState(sNew);
            env.render(agent,aChosen);

        } while (!stepReturn.termState);

        agent.addBellmanErrorItemForEpisodeAndClearPerStepList();
    }

    protected double calcRandomFromIntervall(double minValue, double maxValue) {
        return minValue+Math.random()*(maxValue-minValue);
    }

}
