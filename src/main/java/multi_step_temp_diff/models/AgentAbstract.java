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

@Getter
@Setter
public abstract class AgentAbstract implements AgentNeuralInterface {
    EnvironmentInterface environment;
    int state;
    double discountFactor;
    AgentHelper helper;

    public AgentAbstract(EnvironmentInterface environment, int state, double discountFactor) {
        this.environment = environment;
        this.state = state;
        this.discountFactor = discountFactor;
        this.helper = AgentHelper.builder()
                .nofActions(environment.actionSet().size())
                .environment(environment).discountFactor(discountFactor)
                .readFunction(this::readValue)
                .build();
    }

    @Override
    public int chooseAction(double probRandom) {
        return helper.chooseAction(probRandom,getState());
    }

    @Override
    public int chooseRandomAction() {
        return helper.chooseRandomAction();
    }

    @Override
    public int chooseBestAction(int state) {
        return helper.chooseBestAction(state);
    }

    @Override
    public void updateState(StepReturn stepReturn) {
        setState(stepReturn.newState);
    }


    public abstract double readValue(int state);
    public abstract  void learn(List<NstepExperience> miniBatch);
}
