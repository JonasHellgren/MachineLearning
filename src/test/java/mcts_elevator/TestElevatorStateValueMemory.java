package mcts_elevator;

import monte_carlo_tree_search.domains.elevator.ElevatorStateValueMemory;
import monte_carlo_tree_search.domains.elevator.StateElevator;
import monte_carlo_tree_search.domains.elevator.VariablesElevator;
import monte_carlo_tree_search.generic_interfaces.StateInterface;
import monte_carlo_tree_search.network_training.Experience;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestElevatorStateValueMemory {

    ElevatorStateValueMemory<VariablesElevator> memory;
    @Before
    public void init() {

        memory=new ElevatorStateValueMemory<>(0,1);

    }

    @Test
    public void  givenExperienceSoCEqualValue_whenLearn_thenCorrect() {

        List<Experience<VariablesElevator, Integer>> miniBatch=new ArrayList<>();
        miniBatch.add(createExperience(0,0));
        miniBatch.add(createExperience(0.2,0.2));
        miniBatch.add(createExperience(0.5,0.5));
        miniBatch.add(createExperience(0.9,0.9));

        for (int i = 0; i <1000 ; i++) {
            memory.learn(miniBatch);
        }

        System.out.println("memory.read(state) = " + memory.read(getState(0.2)));
        System.out.println("memory.read(state) = " + memory.read(getState(0.3)));
        System.out.println("memory.read(state) = " + memory.read(getState(0.6)));


    }

    @NotNull
    private StateInterface<VariablesElevator> getState(double SoE) {
        StateInterface<VariablesElevator> state= StateElevator.newFromVariables(VariablesElevator.builder().SoE(SoE).build());
        return state;
    }

    private Experience<VariablesElevator, Integer> createExperience(double SoE, double value) {
        Experience<VariablesElevator, Integer> exp= Experience.<VariablesElevator, Integer>builder()
                .stateVariables(VariablesElevator.builder().SoE(SoE).build())
                .value(value)
                .build();
        return exp;
    }


}
