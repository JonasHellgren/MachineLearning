package safe_rl.helpers;

import lombok.NonNull;
import safe_rl.domain.value_classes.Experience;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import static common.list_arrays.ListUtils.discountedElements;
import static common.list_arrays.ListUtils.getReturns;

/**
 *  Fills in value field in experience
 *  See corresponding test for more
 */

public class ReturnCalculator<S> {

    public List<Experience<S>>  createExperienceListWithReturns(@NonNull List<Experience<S>> experienceList,
                                                                double gamma) {
        List<Experience<S>> experienceListNew=new ArrayList<>();
        List<Double> rewards=experienceList.stream()
                .map(e-> e.isSafeCorrected()? e.arsCorrected().orElseThrow().reward(): e.ars().reward())
                .toList();
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
