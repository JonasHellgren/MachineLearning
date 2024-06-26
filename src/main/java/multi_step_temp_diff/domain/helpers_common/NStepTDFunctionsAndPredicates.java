package multi_step_temp_diff.domain.helpers_common;

import common.other.Counter;
import multi_step_temp_diff.domain.trainer_valueobj.NStepNeuralAgentTrainerSettings;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class NStepTDFunctionsAndPredicates {

    public static BiPredicate<Integer, Integer> isNotAtTerminationTime = (t, tTerm) -> t < tTerm;
    public static BiFunction<Integer, Integer, Integer> timeForUpdate = (t, n) ->
            t - n + 1;
    public static Predicate<Integer> isPossibleToGetExperience = (tau) -> tau >= 0;
    public static BiPredicate<Integer, Integer> isAtTimeJustBeforeTermination = (tau, tTerm) -> tau == tTerm - 1;
    public static BiPredicate<Counter,Integer> isNotToManySteps = (c,maxSteps) ->  c.getCount()< maxSteps;
    public static BiPredicate<Integer, Integer> isTimeToBackUpFromAtOrBeforeTermination = (t, tTerm) -> t < tTerm; //<=
    public static BiPredicate<Integer, NStepNeuralAgentTrainerSettings> isEnoughItemsInBuffer = (n, s) ->
    n > s.batchSize();
    public static Predicate<Integer> isUpdatePossible = (tau) -> tau>=0;


}
