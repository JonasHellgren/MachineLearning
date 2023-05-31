package monte_carlo_tree_search.domains.elevator;

import lombok.Getter;

@Getter
public class PersonCounter {

    int count;
    int stepsBetween;

    public static PersonCounter newCounter(int stepsBetween) {
        return new PersonCounter(stepsBetween);
    }

    public PersonCounter(int stepsBetween) {
        this.stepsBetween = stepsBetween;
        reset();
    }

    boolean isNofStepsBetweenReached() {
        return (count == stepsBetween);
    }

    void update() {
        if (isNofStepsBetweenReached()) {
            reset();
        } else {
            increase();
        }
    }

    void increase() {
        count++;
    }

    void reset() {
        count = 0;
    }

}
