package monte_carlo_tree_search.domains.elevator;

import java.util.Objects;

public class ElevatorTriPredicates {

    @FunctionalInterface
    public interface TriPredicate<T, U, W> {
        boolean test(T t, U u, W w);
        default TriPredicate<T, U, W> negate() {
            return (T t, U u, W w) -> !test(t, u,w);
        }
        default TriPredicate<T, U, W>  and(TriPredicate<T, U, W> other) {
            Objects.requireNonNull(other);
            return (T t, U u, W w) -> test(t, u,w) && other.test(t, u,w);
        }
    }

    public static TriPredicate<Integer,Integer, Double> isAtTop  = (s, p,soe) -> p == EnvironmentElevator.MAX_POS;
    public static TriPredicate<Integer,Integer, Double> isAtBottom  = (s, p,soe) -> p == EnvironmentElevator.MIN_POS;
    public static TriPredicate<Integer,Integer, Double> isNotAtBottom  = isAtBottom.negate();
    public static TriPredicate<Integer,Integer, Double> isStill  = (s, p,soe) -> s == 0;
    public static TriPredicate<Integer,Integer, Double> isMovingUp  = (s, p,soe)  -> s > 0;
    public static TriPredicate<Integer,Integer, Double> isMovingDown  = (s, p,soe) -> s < 0;
    public static TriPredicate<Integer,Integer, Double> isAtFloor  = (s, p,soe) -> (p % EnvironmentElevator.NOF_POS_BETWEEN_FLOORS == 0);
    public static TriPredicate<Integer,Integer, Double> isNotAtFloor  =  isAtFloor.negate();
    public static TriPredicate<Integer,Integer, Double> isSoEOk  = (s, p,soe) -> soe>EnvironmentElevator.SOC_LOW*2;
    public static TriPredicate<Integer,Integer, Double> isSoENotOk  = isSoEOk.negate();

}
