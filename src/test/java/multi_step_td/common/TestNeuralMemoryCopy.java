package multi_step_td.common;

import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.neural_memory.NetworkMemoryInterface;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import multi_step_temp_diff.domain.helpers_specific.*;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.NOF_LAYERS;
import static multi_step_temp_diff.domain.helpers_specific.ChargeAgentParameters.NOF_NEURONS_HIDDEN;
import static org.junit.Assert.assertEquals;

public class TestNeuralMemoryCopy {

    private static final int BUFFER_SIZE = 10_000;
    private static final int BATCH_LENGTH = 100;
    public static final double DELTA = 5;
    public static final double SOC_LIMIT = 0.4;
    public static final int LENGTH_FILTER_WINDOW = 100;
    public static final int TIME_BUDGET = 500;
    public static final int NOF_POINTS_TO_ASSERT = 5;

    AgentNeuralInterface<ChargeVariables> agent;
    AgentChargeNeural agentCasted;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironmentLambdas lambdas;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings envSettings;
    ChargeStateToValueFunctionContainer container;
    ChargeAgentNeuralHelper helper;
    ChargeAgentFactory factory;

    @BeforeEach
    public void init() {
        envSettings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(envSettings);
        environmentCasted = (ChargeEnvironment) environment;
        lambdas=new ChargeEnvironmentLambdas(envSettings);
        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        factory=ChargeAgentFactory.builder().environment(environment).envSettings(envSettings).build();
        agent=factory.buildAgent(initState, NOF_LAYERS, NOF_NEURONS_HIDDEN);
        agentCasted = (AgentChargeNeural) agent;
        container= new ChargeStateToValueFunctionContainer(lambdas, envSettings, SOC_LIMIT);
    }

    @Test
    @Tag("nettrain")
    public void givenFixedValue_whenTrainAndCopied_thenCorrect() {
        helper= createHelper(agent);
        helper.setAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET, container.fixedAtZero);  //the agent is trained towards always zero out

        NetworkMemoryInterface<ChargeVariables> memoryCopy=agentCasted.getMemory().copy();
        assertMemory(container.fixedAtZero,memoryCopy);

    }

    @Test
    @Tag("nettrain")
    public void givenFixedValue_whenCopyMemory_thenCopyNotAffectedWhenTrainingAgentAgain() {
        helper= createHelper(agent);
        helper.setAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET, container.fixedAtZero);  //the agent is trained towards zero
        NetworkMemoryInterface<ChargeVariables> memoryCopy=agentCasted.getMemory().copy();
        helper.setAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET, container.fixedAtMinusTen);  //the agent is trained towards -10

        printMemoryAtRandomPoints(memoryCopy);  //should print mostly zeros
        printMemoryAtRandomPoints(agentCasted.getMemory());  //should print mostly -10

        assertMemory(container.fixedAtMinusTen,agentCasted.getMemory());
        assertMemory(container.fixedAtZero,memoryCopy);
    }

    @Test
    @Tag("nettrain")
    public void givenFixedValue_whenMemoryWeightsCopied_thenCopyAffected() {
        helper= createHelper(agent);
        helper.setAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET, container.fixedAtZero);  //the agent is trained towards zero

        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        AgentNeuralInterface<ChargeVariables>  agent2=factory.buildAgent(initState, NOF_LAYERS, NOF_NEURONS_HIDDEN);
        AgentChargeNeural agent2Casted = (AgentChargeNeural) agent2;

        ChargeAgentNeuralHelper helper2= createHelper(agent2);

        helper.setAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET, container.fixedAtZero);  //agent is trained towards 0
        helper2.setAgentMemory(envSettings,BUFFER_SIZE, TIME_BUDGET, container.fixedAtMinusTen);  //agent2 is trained towards -10

        assertMemory(container.fixedAtMinusTen,agent2Casted.getMemory());

        printMemoryAtRandomPoints(agentCasted.getMemory());  //should print zeros
        printMemoryAtRandomPoints(agent2Casted.getMemory());  //should print -10
        agent2Casted.getMemory().copyWeights(agentCasted.getMemory());   //core line we want to assert
        printMemoryAtRandomPoints(agent2Casted.getMemory());  //should print zeros, now weights from agent

        assertMemory(container.fixedAtZero,agentCasted.getMemory());
        assertMemory(container.fixedAtZero,agent2Casted.getMemory());
    }

    private ChargeAgentNeuralHelper createHelper(AgentNeuralInterface<ChargeVariables> agent) {
        return ChargeAgentNeuralHelper.builder()
                .agent(agent).batchLength(BATCH_LENGTH).filterWindowLength(LENGTH_FILTER_WINDOW).build();
    }

    private void printMemoryAtRandomPoints(NetworkMemoryInterface<ChargeVariables> agentCasted) {
        Function<ChargeState, Double> dummy = container.fixedAtMinusTen;
        Map<Integer, Pair<Double, Double>> valueAssertMap = getValueAssertMap(dummy, agentCasted);
        System.out.println("-------------------------");
        valueAssertMap.values().forEach(p -> System.out.println(p.getRight()));
    }


    private void assertMemory(Function<ChargeState, Double> function,  NetworkMemoryInterface<ChargeVariables> memoryCopy) {
        Map<Integer, Pair<Double, Double>> valueAssertMap = getValueAssertMap(function, memoryCopy);
        valueAssertMap.values().forEach(System.out::println);
        valueAssertMap.values().forEach(p -> Assertions.assertEquals(p.getLeft(), p.getRight(), DELTA));
    }

    @NotNull
    private Map<Integer, Pair<Double, Double>> getValueAssertMap(Function<ChargeState, Double> function,
                                                                 NetworkMemoryInterface<ChargeVariables> memory) {
        ChargeStateSuppliers suppliers=new ChargeStateSuppliers(envSettings);
        Map<Integer, Pair<Double,Double>> valueAssertMap=new HashMap<>();
        for (int i = 0; i < NOF_POINTS_TO_ASSERT; i++) {
            ChargeState state = suppliers.randomDifferentSitePositionsAndRandomSoCs();
            double valueLearned = memory.read(state);
            valueAssertMap.put(i,Pair.of(function.apply(state),valueLearned));
        }
        return valueAssertMap;
    }

}
