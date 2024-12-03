package book_rl_explained.lunar_lander.domain.environment;

import lombok.AllArgsConstructor;
import org.mapdb.Atomic;

@AllArgsConstructor
public class StateLunar {

    VariablesLunar variables;

    public static StateLunar of(double y, double spd) {
        return new StateLunar(new VariablesLunar(y, spd));
    }

    public StateLunar copy() {
        return new StateLunar(variables);
    }
}
