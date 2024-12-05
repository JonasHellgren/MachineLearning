package book_rl_explained.lunar_lander.domain.environment;

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
        return new LunarProperties(500.0, 1, 1.62,5000,0d,10,3d,-100,-1);
    }


}
