package java_ai_gym.models_common;

public class MathUtils {

    public static double calcRandomFromIntervall(double minValue, double maxValue) {
        return minValue+Math.random()*(maxValue-minValue);
    }

    public static double clip(double variable, double minValue, double maxValue) {
        double lowerThanMax= Math.min(variable, maxValue);
        return Math.max(lowerThanMax, minValue);
    }

}
