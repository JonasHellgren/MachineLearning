package multi_step_temp_diff.domain.trainer;

import common.*;
import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import multi_step_temp_diff.domain.agent_abstract.AgentTabularInterface;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.agent_abstract.StateInterface;
import multi_step_temp_diff.domain.environment_abstract.StepReturn;
import multi_step_temp_diff.domain.helpers.AgentInfo;
import multi_step_temp_diff.domain.helpers.NStepTDHelper;
import multi_step_temp_diff.domain.trainer_valueobj.NStepTabularAgentTrainerSettings;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static common.Conditionals.executeIfTrue;
import static multi_step_temp_diff.domain.helpers.NStepTDFunctionsAndPredicates.*;

/**
 * https://www.cs.ubc.ca/labs/lci/mlrg/slides/Multi-step_Bootstrapping.pdf
 * https://lcalem.github.io/blog/2018/11/19/sutton-chap07-nstep
 * <p>
 * a potential improvement is to check that start state is valid
 */

@Builder
@Setter
public class NStepTabularAgentTrainer<S> {


    @Builder.Default
    NStepTabularAgentTrainerSettings settings = NStepTabularAgentTrainerSettings.getDefault();
    @NonNull EnvironmentInterface<S> environment;
    @NonNull AgentTabularInterface<S> agent;
    @NonNull Supplier<StateInterface<S>> startStateSupplier;

    AgentInfo<S> agentInfo;
    NStepTDHelper<S> helper;
    LogarithmicDecay decayProb;

    public void train() {
        agentInfo = new AgentInfo<>(agent);
        helper = NStepTDHelper.newFromNofEpisodesAndNofStepsBetween(settings.nofEpis(),
                settings.nofStepsBetweenUpdatedAndBackuped());
        decayProb = new LogarithmicDecay(settings.probStart(), settings.probEnd(), settings.nofEpis());

        while (!helper.episodeCounter.isExceeded()) {
            agent.setState(startStateSupplier.get());
            helper.reset();
            do {
                executeIfTrue(isNotAtTerminationTime.test(helper.getTime(), helper.T), () -> {
                    var stateBeforeUpdate = agent.getState();
                    var stepReturn = helper.chooseActionStepAndUpdateAgent(agent, environment, decayProb);
                    helper.storeExperience(stateBeforeUpdate, stepReturn);
                });
                helper.tau = timeForUpdate.apply(helper.getTime(), settings.nofStepsBetweenUpdatedAndBackuped());
                executeIfTrue(isUpdatePossible.test(helper.tau), () -> {
                    var difference = updateStateValueForStatePresentAtTimeTau();
                    agent.storeTemporalDifference(difference);
                });
                helper.increaseTime();
            } while (!isAtTimeJustBeforeTermination.test(helper.tau, helper.T));
            helper.increaseEpisode();
        }
    }

    public Map<StateInterface<S>, Double> getStateValueMap() {
        AgentInfo<S> agentInfo = new AgentInfo<>(agent);
        return agentInfo.stateValueMap(environment.stateSet());
    }

    private double updateStateValueForStatePresentAtTimeTau() {
        double sumRewards = sumOfRewardsFromTimeToUpdatePlusOne(helper), G = sumRewards;
        int timeBackUpFrom = helper.getTimeBackUpFrom();
        if (isTimeToBackUpFromAtOrBeforeTermination.test(timeBackUpFrom, helper.T)) {
            StateInterface<S> stateAheadToBackupFrom = helper.stateAheadToBackupFrom();
            double discount = Math.pow(agentInfo.getDiscountFactor(), settings.nofStepsBetweenUpdatedAndBackuped());
            G = sumRewards + discount * agent.readValue(stateAheadToBackupFrom);
        }
        final StateInterface<S> stateToUpdate = helper.statesMap.get(helper.tau);
        double valuePresent = agent.readValue(stateToUpdate);
        double difference = G - valuePresent;
        agent.writeValue(stateToUpdate, valuePresent + settings.alpha() * difference);
        return difference;
    }

    private double sumOfRewardsFromTimeToUpdatePlusOne(NStepTDHelper<S> h) {
        Pair<Integer, Integer> iMinMax =
                new Pair<>(h.tau + 1, Math.min(h.tau + settings.nofStepsBetweenUpdatedAndBackuped(), h.T));
        List<Double> returnTerms = new ArrayList<>();
        for (int i = iMinMax.getFirst(); i <= iMinMax.getSecond(); i++) {
            returnTerms.add(Math.pow(agentInfo.getDiscountFactor(), i - h.tau - 1) * h.timeReturnMap.get(i).reward);
        }
        return ListUtils.sumDoubleList(returnTerms);
    }

}
