package safe_rl.runners;

import safe_rl.environments.trading_electricity.SettingsTrading;
import safe_rl.environments.trading_electricity.SettingsTradingPlotter;

public class RunnerPlotSettingsTrading {


    public static void main(String[] args) {
        SettingsTradingPlotter plotter=new SettingsTradingPlotter();
        var settings= SettingsTrading.new24HoursIncreasingPrice();
        plotter.plot(settings);
    }

}
