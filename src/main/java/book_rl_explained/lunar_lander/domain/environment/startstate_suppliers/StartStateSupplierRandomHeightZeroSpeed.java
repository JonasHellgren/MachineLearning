package book_rl_explained.lunar_lander.domain.environment.startstate_suppliers;

import book_rl_explained.lunar_lander.domain.environment.LunarProperties;
import book_rl_explained.lunar_lander.domain.environment.StartStateSupplierI;
import book_rl_explained.lunar_lander.domain.environment.StateLunar;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartStateSupplierRandomHeightZeroSpeed implements StartStateSupplierI   {

    LunarProperties ep;

    public static StartStateSupplierI create(LunarProperties ep) {
        return new StartStateSupplierRandomHeightZeroSpeed(ep);
    }

    @Override
    public StateLunar getStartState() {
        return StateLunar.randomPosAndZeroSpeed(ep);
    }
}
