package policy_gradient_problems.runners;

import common.MovingAverage;
import lombok.extern.java.Log;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import policy_gradient_problems.domain.agent_interfaces.AgentNeuralActorNeuralCriticII;
import policy_gradient_problems.helpers.NeuralActorUpdaterCEMLoss;
import policy_gradient_problems.helpers.NeuralActorUpdaterPPOLoss;
import policy_gradient_problems.helpers.RecorderTrainingProgress;
import policy_gradient_problems.domain.value_classes.TrainerParameters;
import policy_gradient_problems.environments.cart_pole.*;
import java.util.List;


@Log
public class RunnerTrainersPole {

    static final int LENGTH_WINDOW = 5;
    static final int NOF_STEPS_MAX = 100;
    static final int NOF_EPISODES = 200;
     static final TrainerParameters PARAMETERS_TRAINER = TrainerParameters.builder()
            .nofEpisodes(NOF_EPISODES).nofStepsMax(NOF_STEPS_MAX).gamma(0.99).stepHorizon(5).build();

    public static void main(String[] args) {
        var parameters = ParametersPole.newDefault();
        var agentParamActor = AgentParamActorPole.newAllZeroStateDefaultThetas(parameters);
        var agentCEM = AgentActorICriticPoleCEM.newDefault(StatePole.newUprightAndStill(parameters));
        var agentPPO = AgentActorICriticPolePPO.newDefault(StatePole.newUprightAndStill(parameters));
        var environment = new EnvironmentPole(ParametersPole.newWithMaxNofSteps(NOF_STEPS_MAX));

        var nofStepsListVanilla = trainVanilla(agentParamActor, environment);
        var nofStepsListBaseline = trainBaseline(agentParamActor, environment);
        //var nofStepsListAC = trainCEM(agentCEM, environment,parameters);
        var nofStepsListPPO = trainPPO(agentPPO, environment,parameters);

        printMemories(agentPPO,environment.getParameters());
        somePrinting(agentPPO,environment.getParameters());

        plotNofStepsVersusEpisode(
                List.of(nofStepsListVanilla, nofStepsListPPO),  //nofStepsListBaseline
                List.of("vanilla",  "ppo"));  //"baseline" "actor critic"
    }

    private static List<Double> trainVanilla(AgentParamActorPole agent, EnvironmentPole environment) {
        var trainerVanilla = TrainerParamActorPole.builder()
                .environment(environment).agent(agent.copy()).parameters(PARAMETERS_TRAINER).build();
        trainerVanilla.train();
        return getFilteredNofSteps(trainerVanilla.getRecorderTrainingProgress());
    }

    private static List<Double> trainBaseline(AgentParamActorPole agent, EnvironmentPole environment) {
        var trainerBaseline = TrainerParamActorParamCriticPole.builder()
                .environment(environment).agent(agent).parameters(PARAMETERS_TRAINER).build();
        trainerBaseline.train();
        return getFilteredNofSteps(trainerBaseline.getRecorderTrainingProgress());
    }

    private static List<Double> trainCEM(AgentNeuralActorNeuralCriticII<VariablesPole> agent,
                                         EnvironmentPole environment,
                                         ParametersPole parameters) {
        var trainerAC = TrainerMultiStepActorCriticPole.builder()
                .environment(environment).agent(agent).parameters(PARAMETERS_TRAINER)
                .actorUpdater(new NeuralActorUpdaterCEMLoss<>()).build();
        log.info("training ac");
        trainerAC.train();
        trainerAC.getAgent().setState(StatePole.newUprightAndStill(parameters));
        return getFilteredNofSteps(trainerAC.getRecorderTrainingProgress());
    }

    private static List<Double> trainPPO(AgentNeuralActorNeuralCriticII<VariablesPole> agent,
                                         EnvironmentPole environment,
                                         ParametersPole parameters) {
        var trainerAC = TrainerMultiStepActorCriticPole.builder()
                .environment(environment).agent(agent).parameters(PARAMETERS_TRAINER)
                .actorUpdater(new NeuralActorUpdaterPPOLoss<>()).build();
        log.info("training ppo");
        trainerAC.train();
        trainerAC.getAgent().setState(StatePole.newUprightAndStill(parameters));
        trainerAC.getRecorderTrainingProgress().plot("Training progress - cart pole PPO");
        return getFilteredNofSteps(trainerAC.getRecorderTrainingProgress());
    }

    private static void plotNofStepsVersusEpisode(List<List<Double>> listList, List<String> titles) {
        var chart = new XYChartBuilder().xAxisTitle("Episode").yAxisTitle("Nof steps").width(500).height(300).build();
        var titlesIterator = titles.iterator();
        for (List<Double> doubles : listList) {
            var series = chart.addSeries(titlesIterator.next(), null, doubles);
            series.setMarker(SeriesMarkers.NONE);
        }
        new SwingWrapper<>(chart).displayChart();
    }

    static void printMemories(AgentNeuralActorNeuralCriticII<VariablesPole> agent, ParametersPole parametersPole) {
        for (int i = 0; i < 5 ; i++) {
            StatePole statePole = StatePole.newAllRandom(parametersPole);
            double valueCritic=agent.criticOut(statePole);

            agent.setState(statePole);
            var probs=agent.getActionProbabilities();
            System.out.println("state = "+statePole+", valueCritic = " + valueCritic+", probs = "+probs);
        }
    }

    static void somePrinting(AgentNeuralActorNeuralCriticII<VariablesPole> agent, ParametersPole parametersPole) {
        StatePole uprightAndStill = StatePole.newUprightAndStill(parametersPole);
        double valAll0=agent.criticOut(uprightAndStill);
        double valBigAngleDot=agent.criticOut(uprightAndStill.copyWithAngleDot(0.2));
        System.out.println("valAll0 = " + valAll0+", valBigAngleDot = " + valBigAngleDot);
    }

    private static List<Double> getFilteredNofSteps(RecorderTrainingProgress recorder) {
        var nofStepsList = recorder.nStepsTraj().stream().map(Number::doubleValue).toList();
        var movingAverage = new MovingAverage(LENGTH_WINDOW, nofStepsList);
        return movingAverage.getFiltered();
    }

}
