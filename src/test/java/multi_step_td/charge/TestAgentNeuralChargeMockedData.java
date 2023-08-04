package multi_step_td.charge;

import common.RandUtils;
import multi_step_temp_diff.domain.agent_abstract.AgentNeuralInterface;
import multi_step_temp_diff.domain.agent_parts.NstepExperience;
import multi_step_temp_diff.domain.agent_parts.ReplayBufferNStep;
import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.agents.charge.AgentChargeNeural;
import multi_step_temp_diff.domain.agents.charge.input_vector_setter.InputSetterSoCAtOccupiedZeroOther;
import multi_step_temp_diff.domain.environment_abstract.EnvironmentInterface;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironment;
import multi_step_temp_diff.domain.environments.charge.ChargeState;
import multi_step_temp_diff.domain.environments.charge.ChargeVariables;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

public class TestAgentNeuralChargeMockedData {

    private static final int BUFFER_SIZE = 1000, NOF_ITERATIONS = 1000;
    private static final int BATCH_LENGTH = 30;
    public static final double DELTA = 2;

    AgentNeuralInterface<ChargeVariables> agent;
    EnvironmentInterface<ChargeVariables> environment;
    ChargeEnvironment environmentCasted;
    ChargeEnvironmentSettings settings;
    int nofSiteNodes;


    @BeforeEach
    public void init() {
        settings = ChargeEnvironmentSettings.newDefault();
        environment = new ChargeEnvironment(settings);
        environmentCasted = (ChargeEnvironment) environment;
        nofSiteNodes = environmentCasted.getSettings().siteNodes().size();
        ChargeState initState = new ChargeState(ChargeVariables.builder().build());
        AgentChargeNeuralSettings agentSettings = AgentChargeNeuralSettings.newDefault();
        agent = AgentChargeNeural.builder()
                .environment(environment).state(initState)
                .agentSettings(agentSettings)
                .inputVectorSetterCharge(
                        new InputSetterSoCAtOccupiedZeroOther(agentSettings, environmentCasted.getSettings()))
                .build();
    }

    @Test
    @Tag("nettrain")
    public void givenFixedValue_whenTrain_thenCorrect() {
        final double value = -10d;
        ReplayBufferNStep<ChargeVariables> buffer = ReplayBufferNStep.<ChargeVariables>builder()
                .buffer(createBuffer(value)).build();

        System.out.println("buffer = " + buffer);

        for (int i = 0; i < NOF_ITERATIONS; i++) {
            agent.learn(buffer.getMiniBatch(BATCH_LENGTH));
        }

        // helper.printStateValues();
        // helper.assertAllStates(value, DELTA);

        for (int i = 0; i < 10; i++) {
            double valueLearned = agent.readValue(stateRandomPosAndSoC(nofSiteNodes));
            System.out.println("valueLearned = " + valueLearned);
            Assertions.assertEquals(value,valueLearned, DELTA);
        }

    }

    @NotNull
    private List<NstepExperience<ChargeVariables>> createBuffer(double value) {
        List<NstepExperience<ChargeVariables>> batch = new ArrayList<>();

        for (int i = 0; i < BUFFER_SIZE; i++) {
            NstepExperience<ChargeVariables> exp = NstepExperience.<ChargeVariables>builder()
                    .stateToUpdate(stateRandomPosAndSoC(nofSiteNodes))
                    .value(value)
                    .build();
            batch.add(exp);
        }
        return batch;
    }

    @NotNull
    private static ChargeState stateRandomPosAndSoC(int nofSiteNodes) {
        return new ChargeState(ChargeVariables.builder()
                .posA(randomPos(nofSiteNodes)).posB(randomPos(nofSiteNodes))
                .socA(randomSoC()).socB(randomSoC())
                .build());
    }

    private static double randomSoC() {
        return RandUtils.getRandomDouble(0, 1);
    }

    private static int randomPos(int nofSiteNodes) {
        return RandUtils.getRandomIntNumber(0, nofSiteNodes);
    }


}