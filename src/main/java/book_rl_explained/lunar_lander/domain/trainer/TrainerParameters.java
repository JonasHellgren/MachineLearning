package book_rl_explained.lunar_lander.domain.trainer;

public record TrainerParameters(
        int nofStepsMax
) {

    public static TrainerParameters defaultParams() {
        return new TrainerParameters(1000);
    }

}
