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

        StateObservedObserved s1=new StateObservedObserved(2,false,17);
        StateObservedObserved s2=new StateObservedObserved(5,false,17);
        StateObservedObserved s3=new StateObservedObserved(12,false,17);
        StateObservedObserved s4=new StateObservedObserved(21,false,17);

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

        StateObservedObserved s1=new StateObservedObserved(12,false,17);
        StateObservedObserved s2=new StateObservedObserved(15,false,17);
        StateObservedObserved s3=new StateObservedObserved(12,false,17);  //not realistic jsut for testing
        StateObservedObserved s4=new StateObservedObserved(23,false,17);

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
