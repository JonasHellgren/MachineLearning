package policy_gradient_problems.helpers;

import lombok.NonNull;
import policy_gradient_problems.domain.value_classes.Experience;
import java.util.*;
import static common.list_arrays.ListUtils.*;

/**
 *  Fills in value field in experience
 *  See corresponding test for more
 */

public class ReturnCalculator<S> {

    public List<Experience<S>>  createExperienceListWithReturns(@NonNull List<Experience<S>> experienceList,
                                                                double gamma) {
        List<Experience<S>> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream().map(e->e.reward()).toList();
        ListIterator<Double> returnsIterator=calcReturns(rewards,gamma).listIterator();
        for (Experience<S> exp:experienceList) {
            experienceListNew.add(exp.copyWithValue(returnsIterator.next()));
        }
        return experienceListNew;
    }

    public List<Double> calcReturns(List<Double> rewards, double gamma) {
        List<Double> returns=getReturns(rewards);
        return discountedElements(returns,gamma);
    }

}
