package safe_rl.persistance;

import org.apache.commons.math3.util.Pair;
import safe_rl.persistance.trade_environment.*;

import java.util.List;


public class ElDataHelper {

    public static Pair<List<Double>,List<Double>> getPricePair(DayId dayId,
                                                        Pair<Integer, Integer> fromToHour,
                                                        Pair<PathAndFile,PathAndFile> energyFcrFilePair) {
        ElPriceRepo repo = ElPriceRepo.empty();
        ElPriceXlsReader reader = ElPriceXlsReader.of(repo);
        reader.readDataFromFile(energyFcrFilePair.getFirst(), ElType.ENERGY);
        reader.readDataFromFile(energyFcrFilePair.getSecond(), ElType.FCR);
        var elDataEnergyEuroPerMwh = repo.pricesFromHourToHour(dayId, fromToHour, ElType.ENERGY);
        var elDataFcrEuroPerMW = repo.pricesFromHourToHour(dayId, fromToHour, ElType.FCR);
        var elDataEnergyEuroPerKwh = repo.fromPerMegaToPerKilo(elDataEnergyEuroPerMwh);
        var elDataFCREuroPerKW = repo.fromPerMegaToPerKilo(elDataFcrEuroPerMW);
        return Pair.create(elDataEnergyEuroPerKwh,elDataFCREuroPerKW);
    }
}
