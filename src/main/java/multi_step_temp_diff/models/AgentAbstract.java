package multi_step_temp_diff.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import multi_step_temp_diff.environments.ForkEnvironment;
import multi_step_temp_diff.helpers.AgentHelper;
import multi_step_temp_diff.interfaces.AgentNeuralInterface;
import multi_step_temp_diff.interfaces.EnvironmentInterface;
import multi_step_temp_diff.interfaces.NetworkMemoryInterface;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AgentAbstract implements AgentNeuralInterface {
    EnvironmentInterface environment;
    int state;
    double discountFactor;
    AgentHelper helper;


    @Override
    public int chooseAction(double probRandom) {
        return 0;
    }

    @Override
    public int chooseRandomAction() {
        return 0;
    }

    @Override
    public int chooseBestAction(int state) {
        return 0;
    }

    @Override
    public void updateState(StepReturn stepReturn) {}

    @Override
    public double readValue(int state) {
        return 0;
    }



    @Override
    public void learn(List<NstepExperience> miniBatch) {

    }
}
