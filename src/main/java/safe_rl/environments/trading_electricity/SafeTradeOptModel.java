package safe_rl.environments.trading_electricity;

import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import com.joptimizer.exception.InfeasibleProblemException;
import com.joptimizer.exception.IterationsLimitException;
import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.OptimizationResponse;
import common.joptimizer.LowerBoundConstraint;
import common.joptimizer.UpperBoundConstraint;
import common.list_arrays.ListUtils;
import common.other.Counter;
import common.other.RandUtils;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.java.Log;
import safe_rl.domain.abstract_classes.OptModelI;
import safe_rl.domain.abstract_classes.StateI;
import java.util.Arrays;
import java.util.List;

/**
 * min f=1/2*(power-powerProposed)^2  w.r.t.
  constraints expressed below (jsut above method constraints)

 * f=1/2*x*M*x+k*x+r=1/2*(power^2-2*powerProposed)+powerProposed^2=
 * 1/2*power^2-1*powerProposed+1/2*powerProposed^2
 */

@Builder
@Log
public class SafeTradeOptModel<V> implements OptModelI<V> {
    public static final int N_VARIABLES = 1;
    public static final int MAX_ITERATION = 100;
    public static final int N_CONSTRAINTS = 5;
    public static final int MAX_NOF_INIT_GUESSES = 1000;
    public static final double K_MARGIN_SOC_MAX = 0.7;  //smaller than 1 <=> underestimation of dSoCMax => safer opt

    @NonNull Double powerMin;
    @NonNull Double powerMax;
    @NonNull SettingsTrading settings;
    @Builder.Default
    Double socMin = 0d;
    @Builder.Default
    Double socMax = 1d;
    @NonNull Double socTerminalMin;
    @Builder.Default
    Double kMargindSocMax= K_MARGIN_SOC_MAX;
    @NonNull Double soc;
    @NonNull Double timeNew;
    @Builder.Default
    Double toleranceOptimization = 1e-5;

    final OptimizationRequest or = new OptimizationRequest();
    final JOptimizer optimizer = new JOptimizer();

    public ConvexMultivariateRealFunction costFunction(double powerProposed) {
        double[][] pMatrix = {{1}};
        double[] kVector = new double[]{-powerProposed};
        double r = 1d / 2d * powerProposed * powerProposed;
        return new PDQuadraticMultivariateRealFunction(pMatrix, kVector, r);
    }

    @Override
    public void setModel(StateI<V> state0) {
        log.fine("setModel, state0="+state0 );
        StateTrading state= (StateTrading) state0;
        this.soc = state.soc();
        this.timeNew=state.time()+settings.dt();
    }

    @Override
    public boolean isAnyViolation(@NonNull Double power) {
        double constraintMax = ListUtils.findMax(getConstraintValues(power)).orElseThrow();
        return constraintMax > 0;
    }

    @Override
    public double correctedPower(@NonNull Double powerProposed) throws JOptimizerException {
        Counter counter = new Counter(MAX_NOF_INIT_GUESSES);
        double randPower;
        boolean violation;
        double powerBattMax = settings.powerBattMax();
        do {
            randPower = RandUtils.getRandomDouble(-powerBattMax, powerBattMax);
            violation = isAnyViolation(randPower);
            counter.increase();
        } while (violation && !counter.isExceeded());
        throwIfFailedInitPointSearch(randPower, counter);
        var response = getOptimizationResponse(randPower,powerProposed);
        return response.getSolution()[0];
    }

    private void throwIfFailedInitPointSearch(double randPower, Counter counter)
            throws IterationsLimitException, InfeasibleProblemException {
        if (counter.isExceeded()) {
            log.fine("timeNew = " + timeNew+", soc = " + soc);
            throw new IterationsLimitException("Nof random init power guesses exceeded, " +
                    "try decrease socTerminalMin and/orpowerCapacityFcr");
        }
        if (isAnyViolation(randPower)) {
            throw new InfeasibleProblemException("Nof feasible init guess found, constraints = "
                    + getConstraintValues(randPower));
        }
    }

    public List<Double> getConstraintValues(@NonNull Double powerProposed1) {
        var constraints = constraints();
        var vector = new DenseDoubleMatrix1D(N_VARIABLES);
        vector.set(0, powerProposed1);
        return Arrays.stream(constraints).map(f -> f.value(vector)).toList();
    }

    /**
     *   powerFcr can be both neg and pos, hence "worst sign" considered in each constraint
     *  [0] power>powerMin+powerFcr
     *  [1] power<powerMax-powerFcr
     *  [2] soc+g*(power-powerFcr)-dSoCPC>socMin  =>
     *              power-powerFcr>(socMin-soc+dSoCPC)/g => power>(socMin+dSoCPC-soc)/g+powerFcr
     *  [3] soc+g*(power+powerFcr)+dSoCPC<socMax  =>
     *              power+powerFcr>(socMax-soc-dSoCPC)/g => power>(socMin-dSoCPC-soc)/g-powerFcr
     *  [4] soc+dSocMax+g*(power-powerFcr)>socTerminalMin =>
                    power-powerFcr>(socTerminalMin-soc-dSocMax)/g => power>....+powerFcr
     */

    ConvexMultivariateRealFunction[] constraints() {
        var s=settings;
        double powerFcr=s.powerAvgFcrExtreme();
        double dSoCPC = s.dSoCPC();
        var inequalities = new ConvexMultivariateRealFunction[N_CONSTRAINTS];
        inequalities[0] = LowerBoundConstraint.ofSingle(powerMin+powerFcr);
        inequalities[1] = UpperBoundConstraint.ofSingle(powerMax-powerFcr);
        inequalities[2] = LowerBoundConstraint.ofSingle(powerToHitSocLimit(socMin+dSoCPC)+powerFcr);
        inequalities[3] = UpperBoundConstraint.ofSingle(powerToHitSocLimit(socMax-dSoCPC)-powerFcr);
        double powerMinSoCTerminal=(socTerminalMin-soc-kMargindSocMax*s.dSocMax(timeNew))/s.gFunction()+powerFcr;
        inequalities[4] = LowerBoundConstraint.ofSingle(powerMinSoCTerminal);
        return inequalities;
    }


    OptimizationResponse getOptimizationResponse(
            double powerInit, double powerProposed) throws JOptimizerException {
        defineRequest(or, powerInit, powerProposed);
        optimizer.setOptimizationRequest(or);
        optimizer.optimize();
        return optimizer.getOptimizationResponse();
    }

    void defineRequest(OptimizationRequest or,double powerInit, double powerProposed) {
        double[] initialPoint = {powerInit};
        or.setMaxIteration(MAX_ITERATION);
        or.setF0(costFunction(powerProposed)); // Set the objective function
        or.setFi(constraints());
        or.setInitialPoint(initialPoint); // Optional: initial guess
        or.setToleranceFeas(toleranceOptimization); // Tolerance on feasibility
        or.setTolerance(toleranceOptimization); // Tolerance on optimization
    }


    private double powerToHitSocLimit(Double socMinOrMax) {
        return (socMinOrMax - soc) / settings.gFunction();
    }


}
