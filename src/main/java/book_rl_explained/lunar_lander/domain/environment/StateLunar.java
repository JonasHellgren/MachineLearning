package book_rl_explained.lunar_lander.domain.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hellgren.utilities.random.RandUtils;
import org.mapdb.Atomic;

import java.util.List;

@AllArgsConstructor
public class StateLunar {

    VariablesLunar variables;

    public static StateLunar of(double y, double spd) {
        return new StateLunar(new VariablesLunar(y, spd));
    }

    public static StateLunar randomPosAndSpeed(LunarProperties ep) {
        return of(getyRand(ep), getSpdRand(ep));
    }

    public static StateLunar randomPosAndZeroSpeed(LunarProperties ep) {
        return of(getyRand(ep), 0);
    }

    public double y() {
        return variables.y();
    }

    public double spd() {
        return variables.spd();
    }

    public List<Double> asList() {
        return List.of(y(),spd());
    }


    public StateLunar copy() {
        return new StateLunar(variables);
    }


    private static double getSpdRand(LunarProperties ep) {
        return RandUtils.getRandomDouble(-ep.spdMax(), ep.spdMax());
    }

    private static double getyRand(LunarProperties ep) {
        return RandUtils.getRandomDouble(ep.ySurface(), ep.yMax());
    }

    @Override
    public String toString() {
        return "StateLunar{" +
                "y=" + variables.y() +
                ", spd=" + variables.spd() +
                '}';
    }

}
