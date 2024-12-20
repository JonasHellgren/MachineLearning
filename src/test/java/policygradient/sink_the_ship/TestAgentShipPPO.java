package policygradient.sink_the_ship;

import common.list_arrays.Array2ListConverter;
import common.math.MathUtils;
import common.other.NormDistributionSampler;
import common.other.RandUtilsML;
import lombok.extern.java.Log;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import policy_gradient_problems.environments.sink_the_ship.AgentShipPPO;
import policy_gradient_problems.environments.sink_the_ship.EnvironmentShip;
import policy_gradient_problems.environments.sink_the_ship.ShipSettings;
import policy_gradient_problems.environments.sink_the_ship.StateShip;

import java.util.ArrayList;
import java.util.List;


@Log
public class TestAgentShipPPO {
    public static final double TOL = 0.3;
    public static final double HIT_ANGLE_POS0 = 0.3;
    public static final double HIT_ANGLE_POS1 = 0.65;
    public static final double DELTA = 0.2;
    public static final int POS = 0;


    AgentShipPPO agent;
    ShipSettings shipSettings;

    @BeforeEach
    void init() {
        agent = AgentShipPPO.newDefault();
        shipSettings = ShipSettings.newDefault(); //.withDevMaxMeter(500);
    }

    @Test
    void whenActorOut_thenMeanAndStd() {
        var ms = getOut(0);
        System.out.println("ms = " + ms);
        Assertions.assertEquals(2, ms.size());
    }

    @Test
    void whenActorOutInTwoStates_thenDifferentMeanAndStd() {
        StateShip state0 = StateShip.newFromPos(0);
        StateShip state1 = StateShip.newFromPos(1);
        Assertions.assertNotEquals(agent.meanAndStd(state0), agent.meanAndStd(state1));
    }

    @Test
    @Disabled("long time")
    void whenFitCritic_thenCorrect() {
        int nRows = 100;

        double[][] inMat = new double[nRows][shipSettings.nFeatures()];
        double[] out = new double[nRows];
        for (int i = 0; i < nRows; i++) {
            int pos = randomPos();
            inMat[i] = new double[]{pos};
            out[i] = pos;
        }

        for (int i = 0; i < 30; i++) {
            agent.fitCritic(
                    Array2ListConverter.convertDoubleMatToListOfLists(inMat),
                    Array2ListConverter.convertDoubleArrToList(out));
        }

        double val0 = agent.criticOut(StateShip.newFromPos(0));
        double val1 = agent.criticOut(StateShip.newFromPos(1));

        Assertions.assertEquals(0d, val0, TOL);
        Assertions.assertEquals(1d, val1, TOL);
    }

    private int randomPos() {
        return RandUtilsML.getRandomIntNumber(0, shipSettings.nStates());
    }

    /**
     * Using NormalDistribution gives lot of object creation, hence own
     */

    @Test
    void givenProbDistr_thenCorrect() {
        NormalDistribution normalDistribution = new NormalDistribution(0, 1);
        double pdfValue0 = normalDistribution.density(0);
        double pdfValue1 = normalDistribution.density(1);

        double pdfValue0Own = MathUtils.pdf(0, Pair.create(0d, 1d));
        double pdfValue1Own = MathUtils.pdf(1, Pair.create(0d, 1d));

        System.out.println("pdfValue0 = " + pdfValue0);
        System.out.println("pdfValue1 = " + pdfValue1);

        Assertions.assertTrue(pdfValue0 > pdfValue1);
        Assertions.assertEquals(pdfValue0, pdfValue0Own);
        Assertions.assertEquals(pdfValue1, pdfValue1Own);

    }

    @Test
    @Disabled("long time")
    void whenFitActor_thenCorrect() {
        int nIter = 300;
        for (int i = 0; i < nIter; i++) {
            System.out.println("out0 = " + getOut(0));
            var inAndOutMat = createInOutMatWithAtLeastOneHit();
            agent.fitActor(inAndOutMat.getFirst(), inAndOutMat.getSecond());
        }
        log.info("fitted");
        var out0 = getOut(0);
        var out1 = getOut(1);

        Assertions.assertEquals(HIT_ANGLE_POS0, out0.get(0), DELTA);
        Assertions.assertEquals(HIT_ANGLE_POS1, out1.get(0), DELTA);

    }

    private List<Double> getOut(int pos) {
        return agent.actorOut(StateShip.newFromPos(pos));
    }

    public Pair<List<List<Double>>, List<List<Double>>> createInOutMatWithAtLeastOneHit() {
        EnvironmentShip env = new EnvironmentShip(shipSettings);

        List<List<Double>> inMat = new ArrayList<>(new ArrayList<>());
        List<List<Double>> outMat = new ArrayList<>(new ArrayList<>());
        NormDistributionSampler sampler = new NormDistributionSampler();
        boolean isHitting;
        int nHits = 0;

        do {
            int pos = RandUtilsML.getRandomIntNumber(0, EnvironmentShip.POSITIONS.size());
            var meanAndStd = agent.meanAndStd(StateShip.newFromPos(pos));
            double a = sampler.sampleFromNormDistribution(meanAndStd);
            double pdfOld = MathUtils.pdf(a, meanAndStd);
            isHitting = env.isHitting(pos, a);
            double adv = isHitting ? 1 : 0;
            var inList = List.of((double) pos);
            var outList = List.of(a, adv, pdfOld);

            inMat.add(inList);
            outMat.add(outList);
            nHits += isHitting ? 1 : 0;

        } while (nHits < 1);
        return Pair.create(inMat, outMat);
    }

}
