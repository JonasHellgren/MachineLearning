package maze_domain_design;

import maze_domain_design.domain.environment.value_objects.Action;
import maze_domain_design.environments.obstacle_on_road.PropertiesRoad;
import maze_domain_design.environments.obstacle_on_road.StateRoad;
import maze_domain_design.domain.trainer.aggregates.Episode;
import maze_domain_design.domain.trainer.entities.Experience;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestEpisode {

    static PropertiesRoad envp= PropertiesRoad.roadMaze();
    public static final StateRoad S00 = StateRoad.of(0, 0,envp);
    public static final StateRoad S11 = StateRoad.of(1,1,envp);
    public static final Experience EXPERIENCE0 =
            Experience.nonTermWithIdAndSars(0, S00, Action.random(), 0d, S00);
    public static final Experience EXPERIENCE1 =
            Experience.nonTermWithIdAndSars(1, S11, Action.random(), 0d, S11);

    Episode episode;

    @BeforeEach
    void init() {
        episode=new Episode();
    }

    @Test
    void givenEmpty_thenSizeZero() {
        Assertions.assertEquals(0,episode.size());
        Assertions.assertTrue(episode.getIds().isEmpty());
    }

    @Test
    void whenAddingOne_thenSizeOne() {
        episode.addExp(EXPERIENCE0);
        Assertions.assertEquals(1,episode.size());
        Assertions.assertTrue(episode.getIds().contains(0));
        Assertions.assertEquals(1,episode.nextId());
    }

    @Test
    void whenAddingOneAndClearing_thenSizZero() {
        episode.addExp(EXPERIENCE0);
        episode.clear();
        Assertions.assertEquals(0,episode.size());
    }

    @Test
    void whenAddingTwoAndGetting_thenCorrect() {
        episode.addExp(EXPERIENCE0);
        episode.addExp(EXPERIENCE1);
        Assertions.assertEquals(S00,episode.getExp(0).getSars().s());
        Assertions.assertEquals(S11,episode.getExp(1).getSars().s());
        Assertions.assertEquals(2,episode.size());
    }


}
