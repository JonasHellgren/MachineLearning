package safe_rl.golden_search;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import safe_rl.other.FunctionWrapperI;
import safe_rl.other.GoldenSearcher;
import safe_rl.other.SearchSettings;

class TestGoldenSearcher {

    FunctionWrapperI quadFcn1 = (x) -> -Math.pow(x, 2) + 4 * x;
    FunctionWrapperI quadFcn2 = (x) -> -3 * Math.pow(x, 2) + 12 * x - 5;
    FunctionWrapperI expFcn = (x) -> -Math.exp(x);
    FunctionWrapperI lindDecreaseFcn = (x) -> -x;
    FunctionWrapperI stepFcn = (x) -> (x ==0 ) ? 0 : -100;
    FunctionWrapperI quadFcn3 = (x) -> Math.pow((x - 5), 2) + 2;  //has minimum point
    SearchSettings settings=SearchSettings.builder()
            .xMin(0).xMax(10).tol(0.01).nIterMax(100)
            .build();

    @BeforeEach
    void init() {
    }

    @Test
    void whenQuadFcn1_thenXBestIsCloseTo2() {
        var searcher=new GoldenSearcher(quadFcn1,settings);
        double xBest=searcher.searchMax();
        Assertions.assertEquals(2,xBest,settings.tol());
    }

    @Test
    void whenQuadFcn2_thenXBestIsCloseTo2() {
        var searcher=new GoldenSearcher(quadFcn2,settings);
        double xBest=searcher.searchMax();
        Assertions.assertEquals(2,xBest,settings.tol());
    }

    @Test
    void whenQuadFcn3_thenXBestIsCloseTo5() {
        var searcher=new GoldenSearcher(quadFcn3,settings);
        double xBest=searcher.searchMin();
        Assertions.assertEquals(5,xBest,settings.tol());
    }

    @Test
    void whenExpFcn_thenXBestIsCloseTo0() {
        var searcher=new GoldenSearcher(expFcn,settings);
        double xBest=searcher.searchMax();
        Assertions.assertEquals(0,xBest,settings.tol());
    }

    /***
     * Shows that search fails for non unimodal function
     */
    @Test
    void whenStepFcn_thenXBestIsNotCloseTo0() {
        var searcher=new GoldenSearcher(stepFcn,settings);
        double xBest=searcher.searchMax();
        Assertions.assertNotEquals(0,xBest,settings.tol());
    }

    @Test
    void whenLineDecrFcn_thenXBestIsNotCloseTo0() {
        var searcher=new GoldenSearcher(lindDecreaseFcn,settings);
        double xBest=searcher.searchMax();
        Assertions.assertEquals(0,xBest,settings.tol());
    }

}
