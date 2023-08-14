package sandbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;

public class TestCartesian {


    @Test
    public void whenCalculatingCartesianProductOfSets_thenCorrect() {
        Set<Integer> first = ImmutableSet.of(1, 2);
        Set<Integer> second = ImmutableSet.of(30, 40);
        Set<Integer> third = ImmutableSet.of(500, 600);
        Set<Integer> fourth = ImmutableSet.of(7000, 8000);

        Set<List<Integer>> result =
                Sets.cartesianProduct(ImmutableList.of(first, second,third,fourth));

        for (List<Integer> list:result) {
            System.out.println("list = " + list);
        }

        Assertions.assertEquals(2*2*2*2,result.size());

    }

}
