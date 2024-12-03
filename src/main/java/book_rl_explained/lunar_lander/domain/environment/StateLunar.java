package book_rl_explained.lunar_lander.domain.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.mapdb.Atomic;

@AllArgsConstructor
public class StateLunar {

    VariablesLunar variables;

    public static StateLunar of(double y, double spd) {
        return new StateLunar(new VariablesLunar(y, spd));
    }

    public double y() {
        return variables.y();
    }

    public double spd() {
        return variables.spd();
    }

    public StateLunar copy() {
        return new StateLunar(variables);
    }

    @Override
    public String toString() {
        return "StateLunar{" +
                "y=" + variables.y() +
                ", spd=" + variables.spd() +
                '}';
    }

}
