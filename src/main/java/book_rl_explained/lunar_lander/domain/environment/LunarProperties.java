package book_rl_explained.lunar_lander.domain.environment;

import org.hellgren.utilities.list_arrays.ListCreator;

import java.util.List;

import static org.hellgren.utilities.list_arrays.ListCreator.createFromStartToEndWithNofItems;

public record LunarProperties(
        double massLander,
        double dt,
        double g,  //constant of gravity on moon
        double forceMax,
        double ySurface,
        double yMax,
        double spdMax,
        double rewardFail,  //-100
        double rewardStep) {

    public static LunarProperties defaultProps() {
        return new LunarProperties(500.0, 1, 1.62, 5000, 0d, 10, 3d, -100, -1);
    }

    public List<Double> ySpace(int nItems) {
        return ListCreator.createFromStartToEndWithNofItems(ySurface(), yMax(), nItems);
    }

    public List<Double> spdSpace(int nItems) {
        return createFromStartToEndWithNofItems(-spdMax(), spdMax(), nItems);
    }



}
