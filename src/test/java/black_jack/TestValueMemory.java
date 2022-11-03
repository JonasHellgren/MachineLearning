package black_jack;

import black_jack.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestValueMemory {
    public static final double DELTA = 0.1;
    public static final int NOF_UPDATES = 10;
    public static final double ALPHA = 0.9;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;
    ValueMemory valueMemory;

    @Before
    public void init() {
        episode = new Episode();
        returnsForEpisode = new ReturnsForEpisode();
        valueMemory = new ValueMemory(ALPHA);
    }

    @Test
    public void learnMemoryFromOneEpisode() {

        StateObserved s1 = new StateObserved(2, false, 17);
        StateObserved s2 = new StateObserved(5, false, 17);
        StateObserved s3 = new StateObserved(12, false, 17);
        StateObserved s4 = new StateObserved(21, false, 17);

        episode.add(s1, CardAction.hit, 0d);
        episode.add(s2, CardAction.hit, 0d);
        episode.add(s3, CardAction.hit, 0d);
        episode.add(s4, CardAction.stick, 1d);

        returnsForEpisode.appendReturns(episode);

        System.out.println("returnsForEpisode = " + returnsForEpisode);

        for (int i = 0; i < NOF_UPDATES; i++) {
            valueMemory.updateMemory(returnsForEpisode);
        }

        System.out.println("valueMemory = " + valueMemory);

        Assert.assertEquals(4, valueMemory.nofItems());
        Assert.assertEquals(1, valueMemory.getValue(s1), DELTA);
        Assert.assertEquals(1, valueMemory.getValue(s2), DELTA);

    }

    @Test
    public void learnMemoryFromManyEpisodes() {

    }
}
