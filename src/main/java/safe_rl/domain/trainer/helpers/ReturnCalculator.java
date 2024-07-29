package safe_rl.domain.trainer.helpers;

import com.beust.jcommander.internal.Lists;
import lombok.NonNull;
import safe_rl.domain.trainer.value_objects.Experience;

import java.util.List;
import java.util.ListIterator;
import static common.list_arrays.ListUtils.discountedElements;
import static common.list_arrays.ListUtils.getReturns;

/**
 *  Fills in value field in experience
 *  See corresponding test for more
 */

public class ReturnCalculator<V> {

    public List<Experience<V>>  createExperienceListWithReturns(@NonNull List<Experience<V>> experienceList,
                                                                double gamma) {
        List<Experience<V>> experienceListNew= Lists.newArrayList();
        List<Double> rewards=experienceList.stream()
                .map(e-> e.isSafeCorrected()? e.arsCorrected().orElseThrow().reward(): e.ars().reward())
                .toList();
        ListIterator<Double> returnsIterator=calcReturns(rewards,gamma).listIterator();
        for (Experience<V> exp:experienceList) {
            experienceListNew.add(exp.copyWithValue(returnsIterator.next()));
        }
        return experienceListNew;
    }

    public List<Double> calcReturns(List<Double> rewards, double gamma) {
        List<Double> returns=getReturns(rewards);
        return discountedElements(returns,gamma);
    }

}
