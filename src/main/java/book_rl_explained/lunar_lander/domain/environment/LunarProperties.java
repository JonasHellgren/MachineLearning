package book_rl_explained.lunar_lander.domain.environment;

import lombok.With;
import java.util.List;
import static org.hellgren.utilities.list_arrays.ListCreator.createFromStartToEndWithNofItems;

@With
public record LunarProperties(
        double massLander,  //kg
        double dt,  //seconds
        double g,  //constant of gravity at moon
        double forceMax,  //N
        double ySurface,  //m
        double yMax,  //m
        double spdMax,  //m/s
        double spdLimitCrash,  //m/s
        double rewardFail,
        double rewardSuccess,
        double rewardStep) {

    public static LunarProperties defaultProps() {
        return new LunarProperties(500.0, 0.2, 1.62, 2000, 0d, 5, 3, 2d, -100, 100, -1);
    }

    public List<Double> ySpace(int nItems) {
        return createFromStartToEndWithNofItems(ySurface(), yMax(), nItems);
    }

    public List<Double> spdSpace(int nItems) {
        return createFromStartToEndWithNofItems(-spdMax(), spdMax(), nItems);
    }



}
