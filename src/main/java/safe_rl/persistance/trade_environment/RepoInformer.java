package safe_rl.persistance.trade_environment;

import java.util.List;

public class RepoInformer {

    ElPriceRepo repo;
    DataBaseInformer energyInformer;
    DataBaseInformer fcrInformer;

    public RepoInformer(ElPriceRepo repo) {
        this.repo = repo;
        this.energyInformer =new DataBaseInformer(repo.energyPriceDatabase);
        this.fcrInformer =new DataBaseInformer(repo.fcrPriceDatabase);
    }

    public double maxPriceAllDays (ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.maxPrice()
                : fcrInformer.maxPrice();
    }

    public double minPriceAllDays (ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.minPrice()
                : fcrInformer.minPrice();
    }

    public List<Double> averagePriceEachDay (ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.averagePriceEachDay()
                : fcrInformer.averagePriceEachDay();
    }

    public List<Double> stdPriceEachDay(ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.stdOfPriceEachDay()
                : fcrInformer.stdOfPriceEachDay();
    }

}
