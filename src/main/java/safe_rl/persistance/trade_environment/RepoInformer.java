package safe_rl.persistance.trade_environment;

import com.google.common.primitives.Doubles;

import java.util.List;
import java.util.Optional;

public class RepoInformer {

    public static final double TOL = 1e-4;
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

    public Optional<DayId> findDayWithEqualAvgPrice(double avgPrice, ElType type)  {
        return repo.idsAll().stream()
                .filter(id -> areDoublesSimilar(averagePrice(id,type),avgPrice, TOL))
                .findFirst();
    }

    public Optional<DayId> findDayWithEqualStdPrice(double avgPrice, ElType type)  {
        return repo.idsAll().stream()
                .filter(id -> areDoublesSimilar(stdPrice(id,type),avgPrice, TOL))
                .findFirst();
    }

    private double stdPrice(DayId id, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.stdPrice(id)
                : fcrInformer.stdPrice(id);
    }

    private double averagePrice(DayId id, ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.averagePrice(id)
                : fcrInformer.averagePrice(id);
    }

    public List<String> uniqueRegions(ElType type) {
        return type.equals(ElType.ENERGY)
                ? energyInformer.uniqueRegions()
                : fcrInformer.uniqueRegions();
    }

    public static boolean areDoublesSimilar(double a, double b, double tol) {
        return Math.abs(a - b) <= tol;
    }

}
