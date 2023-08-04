package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environment_valueobj.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;

import java.util.ArrayList;
import java.util.Optional;

public class PositionMapper {

    AgentChargeNeuralSettings agentSettings;
    ChargeEnvironmentSettings environmentSettings;
    ChargeEnvironmentLambdas lambdas;
    ArrayList<Integer> sitePositions;

    public PositionMapper(AgentChargeNeuralSettings agentSettings, ChargeEnvironmentSettings environmentSettings) {
        this.agentSettings = agentSettings;
        this.environmentSettings=environmentSettings;
        this.lambdas=new ChargeEnvironmentLambdas(environmentSettings);
        this.sitePositions=new ArrayList<>(environmentSettings.siteNodes());
    }

    public Optional<Integer> map(int position) {
        return (lambdas.isPosInSite.test(position))
                ? Optional.of(sitePositions.indexOf(position))
                : Optional.empty();

    }

}
