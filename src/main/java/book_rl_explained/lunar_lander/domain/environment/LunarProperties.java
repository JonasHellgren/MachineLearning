package book_rl_explained.lunar_lander.domain.environment;

public record LunarProperties(
        double massLander,
        double dt,
        double g,  //constant of gravity on moon
        double forceMax,
        double ySurface,
        double spdMax,
        double rewardFail,
        double rewardStep) {

    public static LunarProperties defaultProps() {
        return new LunarProperties(1000.0, 1, 1.62,5000,0d,3d,-100,-1);
    }


}
