package safe_rl.persistance.trade_environment;

public enum ElType {
    ENERGY("Eur/MWh"), FCR("Eur/MW");

    final String unit;

    ElType(String unit) {
        this.unit = unit;
    }
}
