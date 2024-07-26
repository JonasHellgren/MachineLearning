package domain_design_tabular_q_learning.environments.tunnels;

import domain_design_tabular_q_learning.environments.shared.XyPos;
import lombok.Builder;
import lombok.With;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.IntStream;

@Builder
public record PropertiesTunnels(
        @With Pair<Integer, Integer> minMaxX,
        Pair<Integer, Integer> minMaxY,
        Set<XyPos> allPositions,
        Set<XyPos> blockedPositions,
        Set<XyPos> freePositions,
        Map<XyPos,Double> failPositionsRewardMap,  //also free
        Map<XyPos,Double> terminalPositionsRewardMap,  //also free
        Double rewardMove,
        Set<XyPos> startPositions
) {

    public static PropertiesTunnels newDefault() {
        Pair<Integer, Integer> minMaxX = Pair.create(0, 8);
        Pair<Integer, Integer> minMaxY = Pair.create(0, 3);

        return PropertiesTunnels.builder()
                .minMaxX(minMaxX).minMaxY(minMaxY)
                .allPositions(getAllPositions(minMaxX, minMaxY))
                .blockedPositions(getBlockedPositions(minMaxX, minMaxY))
                .freePositions(getFreePositions())
                .failPositionsRewardMap(getFailPositionsRewardMap())
                .terminalPositionsRewardMap(getTerminalPositionsRewardMap())
                .rewardMove(-1d)
                .startPositions(getStartPositions())
                .build();
    }

    public XyPos getRandStartPos() {
        List<XyPos> randPosList = new ArrayList<>(startPositions());
        int randIdx= RandomUtils.nextInt(0, randPosList.size());
        return randPosList.get(randIdx);
    }



    private static Map<XyPos, Double> getFailPositionsRewardMap() {
        Map<XyPos,Double> fpm=new HashMap<>();
        fpm.put(XyPos.of(1,0),-10d);
        fpm.put(XyPos.of(1,2),-10d);
        fpm.put(XyPos.of(5,3),-10d);
        return fpm;
    }

    private static Map<XyPos, Double> getTerminalPositionsRewardMap() {
        Map<XyPos,Double> fpm=new HashMap<>();
        fpm.put(XyPos.of(3,0),9d);
        fpm.put(XyPos.of(8,1),10d);
        return fpm;
    }


    private static Set<XyPos> getStartPositions() {
        Set<XyPos> posSet = new HashSet<>();
        posSet.add(XyPos.of(0,1));
        return posSet;
    }



    public static Set<XyPos> getAllPositions(Pair<Integer, Integer> minMaxX, Pair<Integer, Integer> minMaxY) {
        int xMin = minMaxX.getFirst();
        int xMax = minMaxX.getSecond();
        int yMin = minMaxY.getFirst();
        int yMax = minMaxY.getSecond();
        Set<XyPos> posSet = new HashSet<>();
        for (int y = yMin; y <= yMax; y++) {
            int finalY = y;
            IntStream.rangeClosed(xMin,xMax).forEach(x -> posSet.add(XyPos.of(x, finalY)));
        }
        return posSet;
    }


    private static Set<XyPos> getBlockedPositions(Pair<Integer, Integer> minMaxX, Pair<Integer, Integer> minMaxY) {
        return SetUtils.difference(getAllPositions(minMaxX,minMaxY),getFreePositions());
    }

    public static  Set<XyPos> getFreePositions() {
        Set<XyPos> posSet = new HashSet<>();
        IntStream.rangeClosed(1,3).forEach(x -> posSet.add(XyPos.of(x,0)));
        IntStream.rangeClosed(0,2).forEach(x -> posSet.add(XyPos.of(x,1)));
        IntStream.rangeClosed(4,8).forEach(x -> posSet.add(XyPos.of(x,1)));
        IntStream.rangeClosed(1,4).forEach(x -> posSet.add(XyPos.of(x,2)));
        IntStream.rangeClosed(4,5).forEach(x -> posSet.add(XyPos.of(x,3)));
        return posSet;
    }

    public boolean isTermFail(XyPos pos) {
        return getFailPositionsRewardMap().containsKey(pos);
    }

    public Optional<Double> rewardOfFail(XyPos pos) {
        return getFailPositionsRewardMap().containsKey(pos)
                ? Optional.of(getFailPositionsRewardMap().get(pos))
                : Optional.empty();
    }

    public boolean isTerminalNonFail(XyPos pos) {
        return getTerminalPositionsRewardMap().containsKey(pos);
    }

    public Optional<Double> rewardOfTerminalNonFail(XyPos pos) {
        return getTerminalPositionsRewardMap().containsKey(pos)
                ? Optional.of(getTerminalPositionsRewardMap().get(pos))
                : Optional.empty();

    }
}
