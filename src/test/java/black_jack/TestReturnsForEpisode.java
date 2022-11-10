package black_jack;

import black_jack.enums.CardAction;
import black_jack.models_cards.*;
import black_jack.models_episode.Episode;
import black_jack.models_returns.ReturnsForEpisode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestReturnsForEpisode {

    public static final double DELTA = 0.1;
    Episode episode;
    ReturnsForEpisode returnsForEpisode;

    @Before
    public void init() {
        episode=new Episode();
        returnsForEpisode=new ReturnsForEpisode();
    }

    @Test public void createReturnsForEpisodeAlwaysFirstVisit() {

        StateObserved s1=new StateObserved(2,false,17);
        StateObserved s2=new StateObserved(5,false,17);
        StateObserved s3=new StateObserved(12,false,17);
        StateObserved s4=new StateObserved(21,false,17);

        episode.add(s1, CardAction.hit,0d);
        episode.add(s2,CardAction.hit,0d);
        episode.add(s3,CardAction.hit,0d);
        episode.add(s4,CardAction.stick,1d);

        returnsForEpisode.appendReturns(episode);

        System.out.println("returnsForEpisode = " + returnsForEpisode);

        Assert.assertEquals(4,returnsForEpisode.nofItems());
        Assert.assertEquals(1,returnsForEpisode.getItem(1).returnValue, DELTA);
        Assert.assertEquals(1,returnsForEpisode.getItem(3).returnValue, DELTA);

    }

    @Test public void createReturnsForEpisodeNotAlwaysFirstVisit() {

        StateObserved s1=new StateObserved(12,false,17);
        StateObserved s2=new StateObserved(15,false,17);
        StateObserved s3=new StateObserved(12,false,17);  //not realistic jsut for testing
        StateObserved s4=new StateObserved(23,false,17);

        episode.add(s1,CardAction.hit,0d);
        episode.add(s2,CardAction.hit,0d);
        episode.add(s3,CardAction.hit,0d);
        episode.add(s4,CardAction.stick,-1d);

        returnsForEpisode.appendReturns(episode);

        System.out.println("returnsForEpisode = " + returnsForEpisode);

        Assert.assertEquals(3,returnsForEpisode.nofItems());
        Assert.assertEquals(-1,returnsForEpisode.getItem(1).returnValue, DELTA);
        Assert.assertEquals(-1,returnsForEpisode.getItem(2).returnValue, DELTA);

    }

}
