package multi_step_temp_diff.domain.agents.charge.input_vector_setter;

import multi_step_temp_diff.domain.agent_valueobj.AgentChargeNeuralSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentSettings;
import multi_step_temp_diff.domain.environments.charge.ChargeEnvironmentLambdas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/***
 * Missing sorting sitePositions in constructor gives nasty bug
 * The bug results from the fact that a set is "arbitrary" transformed to list
 */

public class PositionMapper {

    AgentChargeNeuralSettings agentSettings;
    ChargeEnvironmentSettings environmentSettings;
    ChargeEnvironmentLambdas lambdas;
    List<Integer> sitePositions;

    public PositionMapper(AgentChargeNeuralSettings agentSettings, ChargeEnvironmentSettings environmentSettings) {
        this.agentSettings = agentSettings;
        this.environmentSettings=environmentSettings;
        this.lambdas=new ChargeEnvironmentLambdas(environmentSettings);
        this.sitePositions=new ArrayList<>(environmentSettings.siteNodes());
        Collections.sort(this.sitePositions);

    }

    public List<Integer> getSitePositions() {
        return sitePositions;
    }

    public Optional<Integer> map(int position) {
        return (lambdas.isPosInSite.test(position))
                ? Optional.of(sitePositions.indexOf(position))
                : Optional.empty();

    }

}
