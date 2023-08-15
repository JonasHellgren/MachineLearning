package common;

import lombok.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DifferenceCalculator<T extends Number> {

    List<T> list;

    List<Double> diffList;

    public DifferenceCalculator(@NonNull  List<T> list) {
        this.list = list;
        this.diffList=new ArrayList<>();
    }

    public List<Double> calculate() {
        if (list == null || list.size() < 2) {
            throw new IllegalArgumentException("List must contain at least two elements.");
        }


        diffList= IntStream.range(1, list.size())
                .mapToObj(i -> list.get(i).doubleValue() - list.get(i - 1).doubleValue())
                .collect(Collectors.toList());

        return diffList;
    }

    public boolean anyNegativeDifference() {
        if (diffList.isEmpty()) {
            throw new RuntimeException("Diff list not defined");
        }
        return diffList.stream().anyMatch(n -> n<0);
    }


}
