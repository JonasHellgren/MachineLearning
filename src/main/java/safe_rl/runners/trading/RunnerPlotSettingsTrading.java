package safe_rl.runners.trading;

import safe_rl.environments.factories.SettingsTradingFactory;
import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.SettingsTradingPlotter;

public class RunnerPlotSettingsTrading {


    public static void main(String[] args) {
        SettingsTradingPlotter plotter=new SettingsTradingPlotter();
        var settings= SettingsTradingFactory.new24HoursZigSawPrice();
        plotter.plot(settings);
    }

}
