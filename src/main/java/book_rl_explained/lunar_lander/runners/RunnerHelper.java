package book_rl_explained.lunar_lander.runners;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierFromMaxHeight;
import book_rl_explained.lunar_lander.domain.environment.startstate_suppliers.StartStateSupplierRandomHeightZeroSpeed;
import book_rl_explained.lunar_lander.domain.trainer.TrainerDependencies;
import book_rl_explained.lunar_lander.helpers.AgentEvaluator;

public class RunnerHelper {


    public static void evaluate(TrainerDependencies trainerDependencies, LunarProperties ep) {
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierRandomHeightZeroSpeed.create(ep));
        var evaluatorFails = AgentEvaluator.of(trainerDependencies);
        double fractionFails= evaluatorFails.fractionFails(10);
        System.out.println("fractionFails = " + fractionFails);
        trainerDependencies = trainerDependencies.withStartStateSupplier(StartStateSupplierFromMaxHeight.create(ep));
        var evaluatorSim = AgentEvaluator.of(trainerDependencies);
        evaluatorSim.plotSimulation();
    }


}
